package org.scoula.domain.exchange.service.policy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.exchange.dto.request.ExchangeBankRequest;
import org.scoula.domain.exchange.dto.response.exchangeResponse.BankRateInfo;
import org.scoula.domain.exchange.dto.response.exchangeResponse.ExchangeBankResponse;
import org.scoula.domain.exchange.dto.response.exchangeResponse.PolicyResponse;
import org.scoula.domain.exchange.entity.ExchangeRate;
import org.scoula.domain.exchange.entity.Type;
import org.scoula.domain.exchange.mapper.ExchangeRateMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExchangeRateService {

	private final ExchangeRateMapper exchangeRateMapper;
	private final DecimalFormat currencyFormatter = new DecimalFormat("#,###.##");
	private final DecimalFormat rateFormatter = new DecimalFormat("#,##0.####");


	public ExchangeBankResponse calculateExchangeBank(ExchangeBankRequest request,
		BigDecimal amountInUsd,
		BigDecimal exchangeCommissionFee) {

		List<String> banks = List.of("국민은행", "하나은행", "신한은행", "우리은행", "기업은행");
		List<BankRateInfo> rates = new ArrayList<>();

		for (String bank : banks) {
			ExchangeRate baseExchange = exchangeRateMapper
				.findLatestExchangeRate(bank, Type.BASE.name(), request.getTargetCurrency());

			ExchangeRate targetExchangeRate = exchangeRateMapper
				.findLatestExchangeRate(bank, request.getType(), request.getTargetCurrency());

			if (targetExchangeRate == null || baseExchange == null) {
				continue;
			}



			BankRateInfo bankRateInfo = calculateBasicRate(targetExchangeRate,
				baseExchange,
				request.getAmount(),
				amountInUsd,
				exchangeCommissionFee);
			rates.add(bankRateInfo);

		}

		sortBanksByAmount(rates, request.getType());

		return ExchangeBankResponse.builder()
			.requestedAmount(request.getAmount())
			.targetCurrency(request.getTargetCurrency())
			.exchangeType(request.getType())
			.banks(rates)
			.build();
	}


	private BankRateInfo calculateBasicRate(ExchangeRate targetExchange, ExchangeRate baseExchange,
		BigDecimal amountInKRW,
		BigDecimal amountInUsd,
		BigDecimal exchangeCommissionFee) {

		BigDecimal transferFee = getTransferFee(targetExchange.getType(), amountInUsd); 
		BigDecimal amountAfterFee;
		BigDecimal amountChangu;
		BigDecimal changuExchangeCommissionFee = calcChanguTransFee(
			targetExchange.getBankName(),
			amountInUsd);

		if (targetExchange.getType() == Type.SELLCASH ||
			targetExchange.getType() == Type.GETCASH ||
			targetExchange.getType() == Type.BASE) {
			amountAfterFee = amountInKRW;
			amountChangu = amountInKRW;
		} else {
			BigDecimal totalFee = exchangeCommissionFee.add(transferFee);
			amountAfterFee = amountInKRW.subtract(totalFee);

			totalFee = changuExchangeCommissionFee.add(transferFee);
			amountChangu = amountInKRW.subtract(totalFee);
		}

		BigDecimal actualRate = targetExchange.getExchangeValue();
		BigDecimal finalAmount = amountAfterFee.divide(actualRate, 2, RoundingMode.HALF_UP);

		if( finalAmount.compareTo(BigDecimal.ZERO) < 0) {
			finalAmount = BigDecimal.ZERO; 
		}

		
		BigDecimal finalAmountChangu = amountChangu.divide(actualRate, 2, RoundingMode.HALF_UP);

		if( finalAmountChangu.compareTo(BigDecimal.ZERO) < 0) {
			finalAmountChangu = BigDecimal.ZERO; 
		}

		String finalAmountChanguDisplay = String.format("%s %s",
			currencyFormatter.format(finalAmountChangu.setScale(2, RoundingMode.HALF_UP)),
			targetExchange.getTargetExchange());

		String rateDisplay = String.format("%s KRW",
			rateFormatter.format(actualRate));

		String totalAmountDisplay = String.format("%s %s",
			currencyFormatter.format(finalAmount.setScale(2, RoundingMode.HALF_UP)),
			targetExchange.getTargetExchange());

		return BankRateInfo.builder()
			.bankName(targetExchange.getBankName())
			.targetoperation(actualRate)  
			.exchangeRate(rateDisplay)
			.baseoperation(baseExchange.getExchangeValue()) 
			.targetoperation(targetExchange.getExchangeValue()) 
			.changguAmount(finalAmountChanguDisplay)
			.totaloperation(finalAmount.setScale(2, RoundingMode.HALF_UP)) 
			.totalAmount(totalAmountDisplay)
			.policyList(new ArrayList<>()) 
			.build();
	}


	private void sortBanksByAmount(List<BankRateInfo> banks, String exchangeType) {
		if ("RECEIVE".equals(exchangeType) || "SELLCASH".equals(exchangeType)) {
			banks.sort(Comparator.comparing(BankRateInfo::getTotaloperation)
				.thenComparing(Comparator.comparing(BankRateInfo::getTargetoperation).reversed())
			);
		} else {
			banks.sort(Comparator.comparing(BankRateInfo::getTotaloperation).reversed()
				.thenComparing(BankRateInfo::getTargetoperation));
		}
	}




	
	private BigDecimal getTransferFee(Type type, BigDecimal amountInUsd) {
		switch (type) {
			case SEND:
				return BigDecimal.valueOf(8000);   			 
			case RECEIVE:
				if (amountInUsd.compareTo(BigDecimal.valueOf(100)) < 0) {
					return BigDecimal.ZERO; 				 
				} else {
					return BigDecimal.valueOf(10000);		
				}
			case SELLCASH:
			case GETCASH:
				return BigDecimal.ZERO;          
			default:
				return BigDecimal.ZERO;
		}
	}

	
	private BigDecimal calcChanguTransFee(
		String bankName,
		BigDecimal usdAmount) {

		BigDecimal exchangeCommissionFee = BigDecimal.ZERO;


		switch (bankName) {
			case "하나은행":
				exchangeCommissionFee = calculateHanaBankFee(usdAmount);
				break;
			case "국민은행":
				exchangeCommissionFee =  calculateKBBankFee(usdAmount);
				break;
			case "기업은행":
				exchangeCommissionFee =  calculateIBKBankFee(usdAmount);
				break;
			case "우리은행":
				exchangeCommissionFee =  calculateWooriBankFee(usdAmount);
				break;
			case "신한은행":
				exchangeCommissionFee =  calculateShinhanBankFee(usdAmount);
				break;
			default:
				exchangeCommissionFee = BigDecimal.ZERO;

		}

		return exchangeCommissionFee;

	}


	private BigDecimal calculateHanaBankFee(BigDecimal usdAmount) {
		if (usdAmount.compareTo(BigDecimal.valueOf(500)) <= 0) {
			return BigDecimal.valueOf(5000);   
		} else if (usdAmount.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			return BigDecimal.valueOf(10000);   
		} else if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			return BigDecimal.valueOf(15000);  
		} else if (usdAmount.compareTo(BigDecimal.valueOf(10000)) <= 0) {
			return BigDecimal.valueOf(20000);  
		} else if (usdAmount.compareTo(BigDecimal.valueOf(20000)) <= 0) {
			return BigDecimal.valueOf(25000);   
		} else {
			return BigDecimal.valueOf(25000);  
		}
	}

	private BigDecimal calculateKBBankFee(BigDecimal usdAmount) {
		if (usdAmount.compareTo(BigDecimal.valueOf(500)) <= 0) {
			return BigDecimal.valueOf(5000);
		} else if (usdAmount.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			return BigDecimal.valueOf(10000);
		} else if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			return BigDecimal.valueOf(15000);
		} else if (usdAmount.compareTo(BigDecimal.valueOf(10000)) <= 0) {
			return BigDecimal.valueOf(20000);
		} else {
			return BigDecimal.valueOf(25000);
		}

	}


	private BigDecimal calculateShinhanBankFee(BigDecimal usdAmount) {
		if (usdAmount.compareTo(BigDecimal.valueOf(500)) <= 0) {
			return BigDecimal.valueOf(5000);   
		} else if (usdAmount.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			return BigDecimal.valueOf(10000);   
		} else if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			return BigDecimal.valueOf(15000);  
		} else if (usdAmount.compareTo(BigDecimal.valueOf(10000)) <= 0) {
			return BigDecimal.valueOf(20000);   
		} else {
			return BigDecimal.valueOf(25000);  
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

	private BigDecimal calculateWooriBankFee(BigDecimal usdAmount) {
		if (usdAmount.compareTo(BigDecimal.valueOf(500)) <= 0) {
			return BigDecimal.valueOf(5000);   
		} else if (usdAmount.compareTo(BigDecimal.valueOf(2000)) <= 0) {
			return BigDecimal.valueOf(10000);   
		} else if (usdAmount.compareTo(BigDecimal.valueOf(5000)) <= 0) {
			return BigDecimal.valueOf(15000);  
		} else if (usdAmount.compareTo(BigDecimal.valueOf(20000)) <= 0) {
			return BigDecimal.valueOf(20000);   
		} else {
			return BigDecimal.valueOf(25000);   
		}
	}


}
