package org.scoula.domain.exchange.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.exchange.dto.request.ExchangeQuickRequest;
import org.scoula.domain.exchange.dto.response.ExchangeLiveResponse;
import org.scoula.domain.exchange.dto.response.exchangeResponse.BankRateInfo;
import org.scoula.domain.exchange.dto.response.ExchangeQuickResponse;
import org.scoula.domain.exchange.entity.Type;
import org.scoula.domain.exchange.exception.ExchangeErrorCode;
import org.scoula.domain.exchange.mapper.ExchangeRateMapper;
import org.scoula.domain.exchange.dto.request.ExchangeBankRequest;
import org.scoula.domain.exchange.dto.response.exchangeResponse.ExchangeBankResponse;
import org.scoula.domain.exchange.entity.ExchangeRate;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExchangeRateQuickServiceImpl implements ExchangeRateQuickService {

	private final ExchangeRateMapper exchangeRateMapper;
	private final DecimalFormat currencyFormatter = new DecimalFormat("#,###.##");
	private final DecimalFormat rateFormatter = new DecimalFormat("#,##0.####");

	@Override
	public ExchangeBankResponse calculateExchangeBank(ExchangeBankRequest request,
		HttpServletRequest httpServletRequest) {


		List<String> banks = List.of("국민은행", "하나은행", "신한은행", "우리은행", "기업은행");
		List<BankRateInfo> rates = new ArrayList<>();

		for (String bank : banks) {
			ExchangeRate latestExchangeRate = exchangeRateMapper
				.findLatestExchangeRate(bank, request.getType(), request.getTargetCurrency());
			if (latestExchangeRate == null) {
				throw new CustomException(
					ExchangeErrorCode.EXCHANGE_REQUIRED_PARAMETER_MISSING, LogLevel.ERROR,
					httpServletRequest.getHeader("txId"),
					Common.builder()
						.srcIp(httpServletRequest.getRemoteAddr())
						.callApiPath(httpServletRequest.getRequestURI())
						.deviceInfo(httpServletRequest.getHeader("user-agent"))
						.retryCount(0)
						.build());
			}

			BankRateInfo bankRateInfo = calculateBankRate(latestExchangeRate, request.getAmount());

			rates.add(bankRateInfo);

		}

		if (rates.isEmpty()) {
			throw new CustomException(
				ExchangeErrorCode.EXCHANGE_NOT_FOUND, LogLevel.ERROR, null, Common.builder().build()
			);
		}

		List<BankRateInfo> sortedRates = sortByExchangeType(rates, request.getType());

		return ExchangeBankResponse.builder()
			.requestedAmount(request.getAmount())
			.targetCurrency(request.getTargetCurrency())
			.exchangeType(request.getType())
			.banks(sortedRates)
			.build();
	}


	private List<BankRateInfo> sortByExchangeType(List<BankRateInfo> rates, String type) {
		Comparator<BankRateInfo> comparator;

		switch (type) {
			case "SELLCASH":  
			case "SEND":      
			case "BASE":     
				comparator = Comparator.comparing(BankRateInfo::getTargetoperation);
				break;

			case "GETCASH":   
			case "RECEIVE":   
				comparator = Comparator.comparing(BankRateInfo::getTargetoperation).reversed();
				break;

			default:
				comparator = Comparator.comparing(BankRateInfo::getTargetoperation);
		}

		return rates.stream()
			.sorted(comparator)
			.collect(Collectors.toList());
	}

	private BankRateInfo calculateBankRate(ExchangeRate rate, BigDecimal amountInKRW) {
		BigDecimal targetAmount;
		String rateDisplay;

		targetAmount = amountInKRW.divide(rate.getExchangeValue(), 2, RoundingMode.HALF_UP);

		rateDisplay = String.format("1 %s 당 %s KRW",
			rate.getTargetExchange(),
			rateFormatter.format(rate.getExchangeValue()));


		return BankRateInfo.builder()
			.bankName(rate.getBankName())
			.exchangeRate(rateDisplay)
			.targetoperation(targetAmount)
			.totalAmount(String.format("%s %s",
				currencyFormatter.format(targetAmount.setScale(2, RoundingMode.HALF_UP)),
				rate.getTargetExchange()))
			.build();
	}


	@Override
	public ExchangeBankResponse calculateExchangeBankV2(ExchangeBankRequest request,
		HttpServletRequest httpServletRequest) {

		List<String> banks = List.of("국민은행", "하나은행", "신한은행", "우리은행", "기업은행");
		List<BankRateInfo> rates = new ArrayList<>();

		for (String bank : banks) {
			ExchangeRate baseExchange = exchangeRateMapper
				.findLatestExchangeRate(bank, request.getType(), String.valueOf(Type.BASE));

			ExchangeRate targetExchangeRate = exchangeRateMapper
				.findLatestExchangeRate(bank, request.getType(), request.getTargetCurrency());

			if (targetExchangeRate == null|| baseExchange == null) {
				throw new CustomException(
					ExchangeErrorCode.EXCHANGE_REQUIRED_PARAMETER_MISSING, LogLevel.ERROR,
					httpServletRequest.getHeader("txId"),
					Common.builder()
						.srcIp(httpServletRequest.getRemoteAddr())
						.callApiPath(httpServletRequest.getRequestURI())
						.deviceInfo(httpServletRequest.getHeader("user-agent"))
						.retryCount(0)
						.build());
			}

			BankRateInfo bankRateInfo = calculatePolicyOption(targetExchangeRate,baseExchange , request.getAmount());

			rates.add(bankRateInfo);
		}
		return ExchangeBankResponse.builder()
			.requestedAmount(request.getAmount())
			.targetCurrency(request.getTargetCurrency())
			.exchangeType(request.getType())
			.banks(rates)
			.build();
	}

	
	private BankRateInfo calculatePolicyOption(ExchangeRate targetExchange, ExchangeRate baseExchange,
		BigDecimal amountInKRW) {
		BigDecimal transferFee = BigDecimal.valueOf(8000);
		BigDecimal zeroPreferentialRate = BigDecimal.ZERO; 

		BigDecimal amountAfterTransferFee = amountInKRW.subtract(transferFee);

		BigDecimal baseActualRate = targetExchange.getExchangeValue(); 
		BigDecimal baseFinalAmount = amountAfterTransferFee.divide(baseActualRate, 2, RoundingMode.HALF_UP);

		String rateDisplay = String.format("1 %s 당 %s KRW",
			targetExchange.getTargetExchange(),
			rateFormatter.format(baseActualRate));

		String totalAmountDisplay = String.format("%s %s",
			currencyFormatter.format(baseFinalAmount.setScale(2, RoundingMode.HALF_UP)),
			targetExchange.getTargetExchange());

		return BankRateInfo.builder()
			.bankName(targetExchange.getBankName())
			.targetoperation(baseActualRate)  
			.exchangeRate(rateDisplay)
			.totalAmount(totalAmountDisplay)
			.policyList(new ArrayList<>())
			.build();
	}


	@Override
	public ExchangeQuickResponse calculateExchangeQuick(ExchangeQuickRequest request,
		HttpServletRequest httpServletRequest) {
		return calculateExchangeQuickNormal(request);

	}

	@Override
	public List<ExchangeLiveResponse> getLatestExchangeRate(HttpServletRequest request) {

		List<ExchangeLiveResponse> baseExchangeRates = exchangeRateMapper.getExchangeLiveList();

		if (baseExchangeRates.isEmpty()) {
			throw new CustomException(ExchangeErrorCode.EXCHANGE_NOT_FOUND, LogLevel.WARNING, null,
				Common.builder().deviceInfo(request.getHeader("host-agent"))
					.srcIp(request.getRemoteAddr())
					.callApiPath(request.getRequestURI())
					.apiMethod(request.getMethod())
					.deviceInfo(request.getHeader("user-agent"))
					.build());
		}

		for (ExchangeLiveResponse baseExchangeRate : baseExchangeRates) {
			baseExchangeRate.setExchangeRate(
				baseExchangeRate.getExchangeRate().setScale(2, RoundingMode.HALF_UP));
		}

		 return baseExchangeRates;
	}

	private ExchangeQuickResponse calculateExchangeQuickNormal(ExchangeQuickRequest request) {

		ExchangeRate baseRate = exchangeRateMapper.findLatestExchangeRate("KB국민은행", "BASE", request.getTargetCurrency());

		if (baseRate == null) {
			return ExchangeQuickResponse
				.builder()
				.build();
		}

		BigDecimal targetAmount = request.getAmount().divide(
			baseRate.getExchangeValue(), 2, RoundingMode.HALF_UP);

		String rateDisplay = String.format("%s %s",
			currencyFormatter.format(targetAmount.setScale(2, RoundingMode.HALF_UP)),
			request.getTargetCurrency());

		return ExchangeQuickResponse.builder()
			.requestedAmount(request.getAmount())
			.targetCurrency(request.getTargetCurrency())
			.ResultRateDisplay(rateDisplay)
			.build();
	}

}
