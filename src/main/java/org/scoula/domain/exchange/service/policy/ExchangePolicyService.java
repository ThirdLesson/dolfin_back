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


	public ExchangeBankResponse getAllBanksWithPolicy(ExchangeBankResponse bankResponse,
		BigDecimal usdAmount,
		BigDecimal exchangeCommissionFee) {

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

	
	private List<PolicyResponse> calculatePoliciesForBank(
		BankRateInfo bankRateInfo,
		String targetCurrency,
		String exchangeType,
		BigDecimal requestedAmount,
		BigDecimal originalCommissionFee) {

		List<PolicyResponse> policyResponses = new ArrayList<>();

		List<ExchangePolicy> policies = exchangePolicyMapper.findAllPolicyByCond(
			bankRateInfo.getBankName(),
			targetCurrency,
			exchangeType
		);

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


		if ("RECEIVE".equals(exchangeType)|| "SELLCASH".equals(exchangeType)) {
			policyResponses.sort(Comparator.comparing(PolicyResponse::getNumericAmount));
		}
		else  {
			policyResponses.sort(Comparator.comparing(PolicyResponse::getNumericAmount, Comparator.reverseOrder()));
		}

		return policyResponses;
	}

	
	private PolicyResponse calculatePolicyAmount(ExchangePolicy policy,
		BankRateInfo bankRateInfo,
		BigDecimal requestedAmount,
		String exchangeType,
		BigDecimal originalCommissionFee,
		String targetCurrency) {

		try {
			BigDecimal defaultTelegraphFee = new BigDecimal("5000");
			BigDecimal policyTelegraphFee = policy.getBaseTelegraphFee() != null ?
				policy.getBaseTelegraphFee() : defaultTelegraphFee;

			if ("SEND".equals(exchangeType) || "GETCASH".equals(exchangeType)) {

				BigDecimal preferentialRate = calculatePreferentialRate(
					policy, bankRateInfo, exchangeType);

				BigDecimal actualAmount = requestedAmount;

				if ("SEND".equals(exchangeType)) {
					actualAmount = actualAmount.subtract(policyTelegraphFee);

					if (policy.getExchangeCommissionFee() == null) {
						if (originalCommissionFee != null) {
							actualAmount = actualAmount.subtract(originalCommissionFee);
						}
					} else if (originalCommissionFee != null) {
						BigDecimal discountRate = policy.getExchangeCommissionFee()
							.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
						BigDecimal discountedCommissionFee = originalCommissionFee
							.multiply(BigDecimal.ONE.subtract(discountRate));
						actualAmount = actualAmount.subtract(discountedCommissionFee);
					}

					if (actualAmount.compareTo(BigDecimal.ZERO) <= 0) {
						return null;
					}
				}

				BigDecimal foreignAmountWithPolicy = actualAmount
					.divide(preferentialRate, 2, RoundingMode.HALF_UP);

				BigDecimal baseAmount = extractNumericValue(bankRateInfo.getTotalAmount());

				if (foreignAmountWithPolicy.compareTo(baseAmount) > 0) {
					return PolicyResponse.builder()
						.name(policy.getPolicyName())
						.amount(currencyFormatter.format(foreignAmountWithPolicy) + " " + targetCurrency)
						.numericAmount(foreignAmountWithPolicy)
						.build();
				}

			} else if ("SELLCASH".equals(exchangeType) || "RECEIVE".equals(exchangeType)) {

				BigDecimal preferentialRate = calculatePreferentialRate(
					policy, bankRateInfo, exchangeType);

				BigDecimal krwAmountWithPolicy = requestedAmount.multiply(preferentialRate);

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



	
	private BigDecimal calculatePreferentialRate(ExchangePolicy policy,
		BankRateInfo bankRateInfo, String exchangeType) {

		BigDecimal preferentialRate = bankRateInfo.getTargetoperation();

		if (policy.getExchangeDiscountRate() != null &&
			policy.getExchangeDiscountRate().compareTo(BigDecimal.ZERO) > 0) {

			BigDecimal discountRatio = policy.getExchangeDiscountRate()
				.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

			if ("SEND".equals(exchangeType) || "GETCASH".equals(exchangeType)) {
				BigDecimal spread = bankRateInfo.getTargetoperation()
					.subtract(bankRateInfo.getBaseoperation());
				BigDecimal discountedSpread = spread.multiply(BigDecimal.ONE.subtract(discountRatio));
				preferentialRate = bankRateInfo.getBaseoperation().add(discountedSpread);

			} else {
				BigDecimal spread = bankRateInfo.getBaseoperation()
					.subtract(bankRateInfo.getTargetoperation()).abs();
				BigDecimal discountedSpread = spread.multiply(BigDecimal.ONE.subtract(discountRatio));
				preferentialRate = bankRateInfo.getBaseoperation().subtract(discountedSpread);
			}
		}

		return preferentialRate;
	}



	private BigDecimal extractNumericValue(String amountStr) {
		try {
			String cleaned = amountStr.replaceAll("[^0-9.]", "");
			return new BigDecimal(cleaned);
		} catch (Exception e) {
			log.warn("금액 파싱 실패: {}", amountStr);
			return BigDecimal.ZERO;
		}
	}



}
