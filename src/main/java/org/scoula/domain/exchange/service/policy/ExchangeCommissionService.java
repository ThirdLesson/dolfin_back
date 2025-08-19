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


	public ExchangeInformation getExchangeCommissionFee(String bankName,
		String targetCurrency,
		String type,
		BigDecimal krwAmount) {
		BigDecimal exchangeCommissionFee = BigDecimal.ZERO;
		if (!"SEND".equals(type)) {
			exchangeCommissionFee = BigDecimal.ZERO;
		}

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

	
	private BigDecimal convertKrwToUsd(String bankName, BigDecimal krwAmount) {
		ExchangeRate usdRate = exchangeRateMapper.findUsdExchangeRate(bankName);
		BigDecimal usdExchangeRate = usdRate.getExchangeValue();

		return krwAmount.divide(usdExchangeRate, 2, RoundingMode.HALF_UP);
	}

	
	private BigDecimal calculateHanaBankFee(BigDecimal usdAmount) {
		if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			return BigDecimal.valueOf(3000);   
		} else {
			return BigDecimal.valueOf(5000);   
		}

	}

	private BigDecimal calculateKBBankFee(BigDecimal usdAmount) {
		if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			return BigDecimal.valueOf(3000);
		} else {
			return BigDecimal.valueOf(5000);
		}

	}

	
	private BigDecimal calculateShinhanBankFee(BigDecimal usdAmount) {
		if (usdAmount.compareTo(BigDecimal.valueOf(500)) <= 0) {
			return BigDecimal.valueOf(2500);   
		} else if (usdAmount.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			return BigDecimal.valueOf(5000);  
		} else if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			return BigDecimal.valueOf(7500);  
		} else if (usdAmount.compareTo(BigDecimal.valueOf(20000)) <= 0) {
			return BigDecimal.valueOf(10000);   
		} else {
			return BigDecimal.valueOf(12500);   
		}
	}

	private BigDecimal calculateWooriBankFee(BigDecimal usdAmount, String targetCurrency) {
		BigDecimal commissionFee = BigDecimal.valueOf(5000);
		if (usdAmount.compareTo(BigDecimal.valueOf(500)) <= 0) {
			commissionFee = BigDecimal.valueOf(5000);   
		} else if (usdAmount.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			commissionFee =  BigDecimal.valueOf(10000);   
		} else if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			commissionFee = BigDecimal.valueOf(15000);   
		} else {
			commissionFee = BigDecimal.valueOf(20000);  
		}
		if ("USD".equals(targetCurrency) || "JPY".equals(targetCurrency) || "EUR".equals(targetCurrency)) {
			return commissionFee.multiply(BigDecimal.valueOf(0.5)).setScale(0, RoundingMode.HALF_UP);
		} else {
			return commissionFee.multiply(BigDecimal.valueOf(0.7)).setScale(0, RoundingMode.HALF_UP);
		}

	}

	private BigDecimal calculateIBKBankFee(BigDecimal usdAmount) {
		if (usdAmount.compareTo(BigDecimal.valueOf(500)) <= 0) {
			return BigDecimal.valueOf(5000);  
		} else if (usdAmount.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			return BigDecimal.valueOf(10000);  
		} else if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			return BigDecimal.valueOf(15000);   
		} else {
			return BigDecimal.valueOf(200000);   
		}
	}

}
