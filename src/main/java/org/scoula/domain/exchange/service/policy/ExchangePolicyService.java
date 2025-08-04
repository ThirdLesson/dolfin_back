package org.scoula.domain.exchange.service.policy;

import static org.scoula.domain.exchange.exception.ExchangeErrorCode.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.common.protocol.types.Field;
import org.scoula.domain.exchange.dto.response.exchangeResponse.BankRateInfo;
import org.scoula.domain.exchange.dto.response.exchangeResponse.ExchangeBankResponse;
import org.scoula.domain.exchange.dto.response.exchangeResponse.PolicyResponse;
import org.scoula.domain.exchange.entity.ExchangePolicy;
import org.scoula.domain.exchange.entity.Type;
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
import java.util.Comparator;
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
	 * @param bankResponse          환율 계산 응답 정보
	 * @param exchangeCommissionFee 원래 내야하는 송금수수료 (SEND일 경우에만 값이 있음)
	 * @return 정책이 적용된 환율 계산 응답 정보
	 */
	public ExchangeBankResponse getAllBanksWithPolicy(ExchangeBankResponse bankResponse,
		BigDecimal usdAmount,
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

			bankRateInfo.addPolicyResponses(policies);

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


		if ("RECEIVE".equals(exchangeType)|| "GETCASH".equals(exchangeType)) {
			// RECEIVE, GETCASH: 필요한 외화가 적을수록 좋음 (오름차순)
			policyResponses.sort(Comparator.comparing(PolicyResponse::getNumericAmount));
		}
		else  {
			// SEND, SELLCASH: 필요한 외화가 많을수록 좋음 (내림차순)
			policyResponses.sort(Comparator.comparing(
				PolicyResponse::getNumericAmount, Comparator.reverseOrder()));
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
			// 인터넷 기본 전신료 (없으면 5000원)
			BigDecimal defaultTelegraphFee = new BigDecimal("5000");
			BigDecimal policyTelegraphFee = policy.getBaseTelegraphFee() != null ?
				policy.getBaseTelegraphFee() : defaultTelegraphFee;

			if ("SEND".equals(exchangeType) || "GETCASH".equals(exchangeType)) {
				// 원화 → 외화 (SEND: 해외송금, GETCASH: 현찰구매)

				// 1. 우대 환율 계산
				BigDecimal preferentialRate = calculatePreferentialRate(
					policy, bankRateInfo, exchangeType);

				// 2. 실제 환전 가능 금액 계산
				BigDecimal actualAmount = requestedAmount;

				// SEND의 경우에만 수수료 차감
				if ("SEND".equals(exchangeType)) {
					// 전신료 차감
					actualAmount = actualAmount.subtract(policyTelegraphFee);

					// 환전 수수료 우대 적용
					if (policy.getExchangeCommissionFee() == null) {
						// 환전 수수료 우대가 없으면 기본 수수료 차감
						if (originalCommissionFee != null) {
							actualAmount = actualAmount.subtract(originalCommissionFee);
						}
					} else if (originalCommissionFee != null) {
						// 환전 수수료 우대 적용
						BigDecimal discountRate = policy.getExchangeCommissionFee()
							.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
						// 원래 수수료에서 우대율 적용
						BigDecimal discountedCommissionFee = originalCommissionFee
							.multiply(BigDecimal.ONE.subtract(discountRate));
						// 실제 금액에서 차감
						actualAmount = actualAmount.subtract(discountedCommissionFee);
					}

					// 음수가 되면 정책 적용 불가
					if (actualAmount.compareTo(BigDecimal.ZERO) <= 0) {
						return null;
					}
				}

				// 3. 우대환율로 환전한 외화 금액
				BigDecimal foreignAmountWithPolicy = actualAmount
					.divide(preferentialRate, 2, RoundingMode.HALF_UP);

				// 4. 기본 금액과 비교
				BigDecimal baseAmount = extractNumericValue(bankRateInfo.getTotalAmount());

				// 기본 금액보다 많이 받을 때만 정책 표시
				if (foreignAmountWithPolicy.compareTo(baseAmount) > 0) {
					return PolicyResponse.builder()
						.name(policy.getPolicyName())
						.amount(currencyFormatter.format(foreignAmountWithPolicy) + " " + targetCurrency)
						.numericAmount(foreignAmountWithPolicy)
						.build();
				}

			} else if ("SELLCASH".equals(exchangeType) || "RECEIVE".equals(exchangeType)) {
				// 외화 → 원화: 높은 환율이 유리

				BigDecimal preferentialRate = calculatePreferentialRate(
					policy, bankRateInfo, exchangeType);

				// 우대환율로 환전한 원화 금액
				BigDecimal krwAmountWithPolicy = requestedAmount.multiply(preferentialRate);

				// 기본 원화 금액
				BigDecimal baseKrwAmount = requestedAmount.multiply(bankRateInfo.getTargetoperation());

				if (krwAmountWithPolicy.compareTo(baseKrwAmount) > 0) {
					return PolicyResponse.builder()
						.name(policy.getPolicyName())
						.amount(currencyFormatter.format(krwAmountWithPolicy) + " KRW")
						.numericAmount(krwAmountWithPolicy)
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



	/**
	 * 우대 환율 계산
	 */
	private BigDecimal calculatePreferentialRate(ExchangePolicy policy,
		BankRateInfo bankRateInfo, String exchangeType) {

		BigDecimal preferentialRate = bankRateInfo.getTargetoperation();

		if (policy.getExchangeDiscountRate() != null &&
			policy.getExchangeDiscountRate().compareTo(BigDecimal.ZERO) > 0) {

			BigDecimal discountRatio = policy.getExchangeDiscountRate()
				.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

			if ("SEND".equals(exchangeType) || "GETCASH".equals(exchangeType)) {
				// 원화 → 외화: 낮은 환율이 유리
				BigDecimal spread = bankRateInfo.getTargetoperation()
					.subtract(bankRateInfo.getBaseoperation());
				BigDecimal discountedSpread = spread.multiply(BigDecimal.ONE.subtract(discountRatio));
				preferentialRate = bankRateInfo.getBaseoperation().add(discountedSpread);

			} else {
				// 외화 → 원화: 높은 환율이 유리
				BigDecimal spread = bankRateInfo.getBaseoperation()
					.subtract(bankRateInfo.getTargetoperation()).abs();
				BigDecimal discountedSpread = spread.multiply(BigDecimal.ONE.subtract(discountRatio));
				preferentialRate = bankRateInfo.getBaseoperation().subtract(discountedSpread);
			}
		}

		return preferentialRate;
	}


	/**
	 * amount 문자열에서 숫자 값 추출
	 */
	private BigDecimal extractNumericValue(String amountStr) {
		try {
			// "1,396.36 USD" -> "1396.36" 추출
			String cleaned = amountStr.replaceAll("[^0-9.]", "");
			return new BigDecimal(cleaned);
		} catch (Exception e) {
			log.warn("금액 파싱 실패: {}", amountStr);
			return BigDecimal.ZERO;
		}
	}



}
