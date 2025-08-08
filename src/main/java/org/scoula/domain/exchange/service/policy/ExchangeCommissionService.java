package org.scoula.domain.exchange.service.policy;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.scoula.domain.exchange.entity.ExchangeRate;
import org.scoula.domain.exchange.mapper.ExchangeRateMapper;
import org.scoula.domain.exchange.service.finalService.ExchangeInformation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExchangeCommissionService {

	private final ExchangeRateMapper exchangeRateMapper;

	/**
	 * 송금 수수료 조회 (원화 금액을 USD로 환산하여 구간별 수수료 계산)
	 *
	 * @param bankName  은행 이름
	 * @param type      환율 타입
	 * @param krwAmount 원화 금액
	 * @return 원화 기준 송금 수수료
	 */
	public ExchangeInformation getExchangeCommissionFee(String bankName,
		String targetCurrency,
		String type,
		BigDecimal krwAmount) {
		// 송금(SEND) 타입이 아니면 수수료 없음
		BigDecimal exchangeCommissionFee = BigDecimal.ZERO;
		if (!"SEND".equals(type)) {
			exchangeCommissionFee = BigDecimal.ZERO;
		}

		// 원화를 USD로 환산
		BigDecimal usdAmount = convertKrwToUsd(bankName, krwAmount);

		switch (bankName) {
			case "하나은행":
				exchangeCommissionFee = calculateHanaBankFee(usdAmount);
				break;
			case "국민은행":
				exchangeCommissionFee = calculateKBBankFee(usdAmount);
				break;
			case "기업은행":
				exchangeCommissionFee = calculateWooriBankFee(usdAmount,targetCurrency);
				break;
			case "우리은행":
				exchangeCommissionFee = calculateIBKBankFee(usdAmount);
				break;
			case "신한은행":
				exchangeCommissionFee = calculateShinhanBankFee(usdAmount);
				break;
			default:
				exchangeCommissionFee = BigDecimal.ZERO;
		}

		return ExchangeInformation.builder()
			.exchangeCommissionFee(exchangeCommissionFee)
			.usdAmount(usdAmount)
			.build();

	}

	/**
	 * 원화를 USD로 환산
	 */
	private BigDecimal convertKrwToUsd(String bankName, BigDecimal krwAmount) {
		// 해당 은행의 USD 기준 환율 조회
		ExchangeRate usdRate = exchangeRateMapper.findUsdExchangeRate(bankName);
		BigDecimal usdExchangeRate = usdRate.getExchangeValue();

		// 원화 ÷ 환율 = USD
		return krwAmount.divide(usdExchangeRate, 2, RoundingMode.HALF_UP);
	}

	/**
	 * 하나은행 송금 수수료 계산
	 */
	// 하나은행 인터넷 수수료 계산
	// 미화 5,000불 상당액 이하 : 3,000원
	// 미화 5,000불 상당액 초과 : 5,000원
	private BigDecimal calculateHanaBankFee(BigDecimal usdAmount) {
		if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			return BigDecimal.valueOf(3000);   // USD 5천불 이하
		} else {
			return BigDecimal.valueOf(5000);   // USD 2만불 초과
		}

	}

	// KB국민은행 수수료 계산 예시
	// 미화 5,000불 상당액 이하 : 3,000원
	// 미화 5,000불 상당액 초과 : 5,000원
	private BigDecimal calculateKBBankFee(BigDecimal usdAmount) {
		if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			return BigDecimal.valueOf(3000);
		} else {
			// USD 5천불 초과
			return BigDecimal.valueOf(5000);
		}

	}

	// 신한은행 수수료 계산
	// 미화 5백불 상당액 이하: 2,500원
	// 미화 2천불 상당액 이하: 5,000원
	// 미화 5천불 상당액 이하: 7,500원
	// 미화 2만불 상당액 이하: 10,000원
	// 미화 2만불 상당액 초과: 12,500원
	private BigDecimal calculateShinhanBankFee(BigDecimal usdAmount) {
		if (usdAmount.compareTo(BigDecimal.valueOf(500)) <= 0) {
			return BigDecimal.valueOf(2500);    // USD 500 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			return BigDecimal.valueOf(5000);   // USD 2,000 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			return BigDecimal.valueOf(7500);   // USD 5,000 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(20000)) <= 0) {
			return BigDecimal.valueOf(10000);   // USD 10,000 이하
		} else {
			return BigDecimal.valueOf(12500);   // USD 10,000 초과
		}
	}

	// 우리은행 수수료 계산
	private BigDecimal calculateWooriBankFee(BigDecimal usdAmount, String targetCurrency) {
		BigDecimal commissionFee = BigDecimal.valueOf(5000);
		if (usdAmount.compareTo(BigDecimal.valueOf(500)) <= 0) {
			commissionFee = BigDecimal.valueOf(5000);    // USD 500 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			commissionFee =  BigDecimal.valueOf(10000);   // USD 2,000 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			commissionFee = BigDecimal.valueOf(15000);   // USD 5,000 이하
		} else {
			commissionFee = BigDecimal.valueOf(20000);   // USD 5,000 초과
		}
		//  USD, JPY, EUR 50프로 우대
		if ("USD".equals(targetCurrency) || "JPY".equals(targetCurrency) || "EUR".equals(targetCurrency)) {
			return commissionFee.multiply(BigDecimal.valueOf(0.5)).setScale(0, RoundingMode.HALF_UP);
		} else {
			// 다른 통화는 30프로 우대
			return commissionFee.multiply(BigDecimal.valueOf(0.7)).setScale(0, RoundingMode.HALF_UP);
		}

	}

	// 기업은행 수수료 계산
	// 미화 500불 상당액 이하 : 5,000원
	// 미화 2,000불 상당액 이하 : 10,000원
	// 미화 5,000불 상당액 이하 : 15,000원
	// 미화 5,000불 상당액 초과 : 20,000원
	private BigDecimal calculateIBKBankFee(BigDecimal usdAmount) {
		if (usdAmount.compareTo(BigDecimal.valueOf(500)) <= 0) {
			return BigDecimal.valueOf(5000);    // USD 500 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			return BigDecimal.valueOf(10000);   // USD 2,000 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			return BigDecimal.valueOf(15000);   // USD 5,000 이하
		} else {
			return BigDecimal.valueOf(200000);   // USD 20,000 초과
		}
	}

}
