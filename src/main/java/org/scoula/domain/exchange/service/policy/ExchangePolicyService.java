package org.scoula.domain.exchange.service.policy;

import static org.scoula.domain.exchange.exception.ExchangeErrorCode.*;
import static org.scoula.domain.member.exception.MemberErrorCode.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.scoula.domain.exchange.dto.response.exchangeResponse.BankRateInfo;
import org.scoula.domain.exchange.dto.response.exchangeResponse.ExchangeBankResponse;
import org.scoula.domain.exchange.dto.response.exchangeResponse.PolicyResponse;
import org.scoula.domain.exchange.entity.ExchangePolicy;
import org.scoula.domain.exchange.mapper.ExchangePolicyMapper;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExchangePolicyService {

	private final ExchangePolicyMapper exchangePolicyMapper;
	private final DecimalFormat currencyFormatter = new DecimalFormat("#,###.##");
	private final HttpServletRequest httpServletRequest;

	/**
	 * 모든 은행에 대해 적용 가능한 우대정책을 계산하여 반환
	 *
	 * @param bankResponse 환율 계산 응답 정보
	 * @param exchangeCommissionFee 원래 내야하는 송금수수료 (SEND일 경우에만 값이 있음)
	 * @return 정책이 적용된 환율 계산 응답 정보
	 */
	public ExchangeBankResponse getAllBanksWithPolicy(ExchangeBankResponse bankResponse,
		BigDecimal exchangeCommissionFee) {


		// 각 은행별로 정책 적용
		for (BankRateInfo bankRateInfo : bankResponse.getBanks()) {
			List<PolicyResponse> policies = calculatePoliciesForBank(
				bankRateInfo,
				bankResponse.getTargetCurrency(),
				bankResponse.getExchangeType(),
				bankResponse.getRequestedAmount(),
				exchangeCommissionFee
			);

			bankRateInfo.addPolicyResponse(policies);

		}

		return bankResponse;
	}

	/**
	 * 특정 은행에 대한 모든 적용 가능한 정책 계산
	 */
	private List<PolicyResponse> calculatePoliciesForBank(
		BankRateInfo bankRateInfo,
		String targetCurrency,
		String exchangeType,
		BigDecimal requestedAmount,
		BigDecimal originalCommissionFee) {

		List<PolicyResponse> policyResponses = new ArrayList<>();

		// 해당 은행의 활성화된 정책 조회
		List<ExchangePolicy> policies = exchangePolicyMapper.findAllPolicyByCond(
			bankRateInfo.getBankName(),
			targetCurrency,
			exchangeType
		);

		// 각 정책에 대해 적용 금액 계산
		for (ExchangePolicy policy : policies) {
			if (Boolean.TRUE.equals(policy.getIsActive())) {
				PolicyResponse policyResponse = calculatePolicyAmount(
					policy,
					bankRateInfo,
					requestedAmount,
					exchangeType,
					originalCommissionFee,
					targetCurrency
				);

				if (policyResponse != null) {
					policyResponses.add(policyResponse);
				}
			}
		}

		return policyResponses;
	}

	/**
	 * 개별 정책에 대한 적용 금액 계산
	 * 정책 적용 시 실제로 받게 되는 외화 금액을 계산
	 */
	private PolicyResponse calculatePolicyAmount(ExchangePolicy policy,
		BankRateInfo bankRateInfo,
		BigDecimal requestedAmount,
		String exchangeType,
		BigDecimal originalCommissionFee,
		String targetCurrency) {

		try {
			// 기본 전신료 (없으면 10,000원으로 가정)
			BigDecimal defaultTelegraphFee = new BigDecimal("10000");
			BigDecimal policyTelegraphFee = policy.getBaseTelegraphFee() != null ?
				policy.getBaseTelegraphFee() : defaultTelegraphFee;

			if ("SEND".equals(exchangeType)) {
				// SEND 타입: 원화 → 외화

				// 1. 우대 환율 계산
				BigDecimal preferentialRate = bankRateInfo.getTargetoperation(); // 기본값

				if (policy.getExchangeDiscountRate() != null &&
					policy.getExchangeDiscountRate().compareTo(BigDecimal.ZERO) > 0) {

					// 스프레드 계산
					BigDecimal spread = bankRateInfo.getTargetoperation()
						.subtract(bankRateInfo.getBaseoperation());

					// 우대 환율 = 기준환율 + (스프레드 × (1 - 우대율))
					BigDecimal discountRatio = policy.getExchangeDiscountRate()
						.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
					BigDecimal discountedSpread = spread.multiply(BigDecimal.ONE.subtract(discountRatio));
					preferentialRate = bankRateInfo.getBaseoperation().add(discountedSpread);

				}

				// 2. 실제 송금 가능 금액 계산
				// 요청금액에서 전신료와 송금수수료를 뺀 금액
				BigDecimal actualSendAmount = requestedAmount.subtract(policyTelegraphFee);

				// 정책 수수료가 있으면 차감
				if (policy.getExchangeCommissionFee() != null && originalCommissionFee != null) {
					actualSendAmount = actualSendAmount.subtract(policy.getExchangeCommissionFee());
				}

				// 3. 우대환율로 환전한 외화 금액
				BigDecimal foreignAmountWithPolicy = actualSendAmount
					.divide(preferentialRate, 2, RoundingMode.HALF_UP);

				// 4. 기본 totalAmount 파싱 (예: "2,108.11 JPY")
				String totalAmountStr = bankRateInfo.getTotalAmount();
				String cleanAmount = totalAmountStr.replaceAll("[^0-9.]", "");
				BigDecimal baseAmount = new BigDecimal(cleanAmount);

				// 5. 정책 적용 후 실제로 받는 총 외화 금액
				if (foreignAmountWithPolicy.compareTo(baseAmount) > 0) {
					return PolicyResponse.builder()
						.name(policy.getPolicyName())
						.amount(
							currencyFormatter.format(foreignAmountWithPolicy.setScale(2, RoundingMode.HALF_UP))
							+ " " + targetCurrency)
						.build();
				}

			} else if ("GETCASH".equals(exchangeType)) {
				// GETCASH: 원화 → 외화 (현찰 구매)

				BigDecimal preferentialRate = bankRateInfo.getTargetoperation();

				if (policy.getExchangeDiscountRate() != null &&
					policy.getExchangeDiscountRate().compareTo(BigDecimal.ZERO) > 0) {

					BigDecimal spread = bankRateInfo.getTargetoperation()
						.subtract(bankRateInfo.getBaseoperation());
					BigDecimal discountRatio = policy.getExchangeDiscountRate()
						.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
					BigDecimal discountedSpread = spread.multiply(BigDecimal.ONE.subtract(discountRatio));
					preferentialRate = bankRateInfo.getBaseoperation().add(discountedSpread);
				}

				// 우대환율로 환전한 외화 금액
				BigDecimal foreignAmountWithPolicy = requestedAmount
					.divide(preferentialRate, 2, RoundingMode.HALF_UP);

				// 기본 금액
				String totalAmountStr = bankRateInfo.getTotalAmount();
				String cleanAmount = totalAmountStr.replaceAll("[^0-9.]", "");
				BigDecimal baseAmount = new BigDecimal(cleanAmount);

				// 기본 금액보다 많이 받을 때만 정책 표시
				if (foreignAmountWithPolicy.compareTo(baseAmount) > 0) {
					return PolicyResponse.builder()
						.name(policy.getPolicyName())
						.amount(
							currencyFormatter.format(
							foreignAmountWithPolicy.setScale(2, RoundingMode.HALF_UP))
							+ " " + targetCurrency)
						.build();
				}

			} else if ("SELLCASH".equals(exchangeType) || "RECEIVE".equals(exchangeType)) {
				// 외화 → 원화: 높은 환율이 유리

				BigDecimal preferentialRate = bankRateInfo.getTargetoperation();

				if (policy.getExchangeDiscountRate() != null &&
					policy.getExchangeDiscountRate().compareTo(BigDecimal.ZERO) > 0) {

					BigDecimal spread = bankRateInfo.getBaseoperation()
						.subtract(bankRateInfo.getTargetoperation()).abs();
					BigDecimal discountRatio = policy.getExchangeDiscountRate()
						.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
					BigDecimal discountedSpread = spread.multiply(BigDecimal.ONE.subtract(discountRatio));

					// 외화를 원화로 바꿀 때는 환율이 높을수록 유리
					preferentialRate = bankRateInfo.getBaseoperation().subtract(discountedSpread);
				}

				// 우대환율로 환전한 원화 금액
				BigDecimal krwAmountWithPolicy = requestedAmount.multiply(preferentialRate);

				// 기본 원화 금액
				BigDecimal baseKrwAmount = requestedAmount.multiply(bankRateInfo.getTargetoperation());

				// 추가로 받는 원화를 다시 외화로 환산 (비교를 위해)
				BigDecimal additionalKrw = krwAmountWithPolicy.subtract(baseKrwAmount);
				BigDecimal additionalForeign = additionalKrw
					.divide(bankRateInfo.getTargetoperation(), 2, RoundingMode.HALF_UP);

				if (additionalForeign.compareTo(BigDecimal.ZERO) > 0) {
					// 외화→원화의 경우 실제로 받는 원화를 표시하는 것이 더 의미있을 수 있음
					// 하지만 요청에 따라 외화 기준으로 표시
					return PolicyResponse.builder()
						.name(policy.getPolicyName())
						.amount(
							currencyFormatter.format(
								krwAmountWithPolicy.setScale(2, RoundingMode.HALF_UP))
							+ " KRW")  // 또는 원화로 표시
						.build();
				}
			}

		} catch (Exception e) {
			throw new CustomException(EXCHANGE_AMOUNT_SMALL_THAN_ZERO, LogLevel.WARNING, null, Common.builder()
				.srcIp(httpServletRequest.getRemoteAddr())
				.callApiPath(httpServletRequest.getRequestURI())
				.apiMethod(httpServletRequest.getMethod())
				.deviceInfo(httpServletRequest.getHeader("user-Agent"))
				.build());

		}

		return null;
	}
}
