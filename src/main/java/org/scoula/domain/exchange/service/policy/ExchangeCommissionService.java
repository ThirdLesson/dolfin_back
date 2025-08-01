package org.scoula.domain.exchange.service.policy;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.scoula.domain.exchange.entity.ExchangeRate;
import org.scoula.domain.exchange.mapper.ExchangeRateMapper;
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
	 * 특정 은행의 환율 수수료 조회
	 *
	 * @param bankName 은행 이름
	 * @return 환율 수수료 비율 (0.0 ~ 1.0)
	 */
	public BigDecimal getExchangeCommission(String bankName) {
		ExchangeRate exchange = exchangeRateMapper.findUsdExchangeRate(bankName);
		return exchange.getExchangeValue();
	}

	/**
	 * 송금 수수료 조회 (원화 금액을 USD로 환산하여 구간별 수수료 계산)
	 *
	 * @param bankName  은행 이름
	 * @param type      환율 타입
	 * @param krwAmount 원화 금액
	 * @return 원화 기준 송금 수수료
	 */
	public BigDecimal getExchangeCommissionFee(String bankName, String type, BigDecimal krwAmount) {
		// 송금(SEND) 타입이 아니면 수수료 없음
		// SEND나 RECEIVE가 아니면 수수료 없음
		if (!"SEND".equals(type)) {
			return BigDecimal.ZERO;
		}


		// 원화를 USD로 환산
		BigDecimal usdAmount = convertKrwToUsd(bankName, krwAmount);


		switch (bankName) {
			case "하나은행":
				return calculateHanaBankFee(usdAmount);
			case "국민은행":
				return calculateKBBankFee(usdAmount);
			// case "기업은행":
			//     return calculateIBKBankFee(usdAmount);
			// case "우리은행":
			//     return calculateWooriBankFee(usdAmount);
			case "신한은행":
			    return calculateShinhanBankFee(usdAmount);
			default:
				return BigDecimal.ZERO;
		}
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
	private BigDecimal calculateHanaBankFee(BigDecimal usdAmount) {
		// 외화송금
		if (usdAmount.compareTo(new BigDecimal("3000")) <= 0) {
			// 미화3천불 상당액 이하: 진성 3,000원
			return new BigDecimal("3000");
		} else if (usdAmount.compareTo(new BigDecimal("5000")) <= 0) {
			// 미화5천불 상당액 초과: 진성 5,000원 (국외송금시)
			return new BigDecimal("5000");
		}

		// 제휴처송금
		else if (usdAmount.compareTo(new BigDecimal("10000")) <= 0) {
			// 미화 1만불 상당액 이하: 진성 5,000원
			return new BigDecimal("5000");
		}

		// 기본값 (명시되지 않은 구간)
		return new BigDecimal("5000");
	}

	// KB국민은행 수수료 계산 예시
	private BigDecimal calculateKBBankFee(BigDecimal usdAmount) {
		// USD 500달러 상당액 이하	5,000원
		if (usdAmount.compareTo(BigDecimal.valueOf(500)) <= 0) {
			// USD 2,000달러 상당액 이하	10,000원
			return BigDecimal.valueOf(5000);
		} else if (usdAmount.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			// USD 2,000달러 상당액 이하	10,000원
			return BigDecimal.valueOf(10000);
		} else if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			// USD 5,000달러 상당액 이하	15,000원
			return BigDecimal.valueOf(15000);
		} else if (usdAmount.compareTo(BigDecimal.valueOf(10000)) <= 0) {
			// USD 10,000달러 상당액 이하	20,000원
			return BigDecimal.valueOf(20000);
		} else {
			// USD 10,000달러 상당액 초과	25,000원
			return BigDecimal.valueOf(25000);
		}

	}


	// 신한은행 수수료 계산
	private BigDecimal calculateShinhanBankFee(BigDecimal usdAmount) {
		if (usdAmount.compareTo(BigDecimal.valueOf(500)) <= 0) {
			return BigDecimal.valueOf(5000);    // USD 500 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			return BigDecimal.valueOf(10000);   // USD 2,000 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			return BigDecimal.valueOf(15000);   // USD 5,000 이하
		} else if (usdAmount.compareTo(BigDecimal.valueOf(10000)) <= 0) {
			return BigDecimal.valueOf(20000);   // USD 10,000 이하
		} else {
			return BigDecimal.valueOf(25000);   // USD 10,000 초과
		}
	}

}
