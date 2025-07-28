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
import org.scoula.domain.exchange.dto.response.BankRateInfo;
import org.scoula.domain.exchange.dto.response.ExchangeQuickResponse;
import org.scoula.domain.exchange.exception.ExchangeErrorCode;
import org.scoula.domain.exchange.mapper.ExchangeRateMapper;
import org.scoula.domain.exchange.dto.request.ExchangeBankRequest;
import org.scoula.domain.exchange.dto.response.ExchangeBankResponse;
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
public class ExchangeRateServiceImpl implements ExchangeRateService {

	private final ExchangeRateMapper exchangeRateMapper;
	private final DecimalFormat currencyFormatter = new DecimalFormat("#,###.##");
	private final DecimalFormat rateFormatter = new DecimalFormat("#,##0.####");

	@Override
	public ExchangeBankResponse calculateExchangeBank(ExchangeBankRequest request,
		HttpServletRequest httpServletRequest) {

		List<String> banks = List.of("KB국민은행", "하나은행", "신한은행", "우리은행", "기업은행");
		List<BankRateInfo> rates = new ArrayList<>();

		// 각 은행별 환율 정보 조회
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

		// 환율 타입에 따른 정렬
		List<BankRateInfo> sortedRates = sortByExchangeType(rates, request.getType());

		return ExchangeBankResponse.builder()
			.requestedAmount(request.getAmount())
			.targetCurrency(request.getTargetCurrency())
			.exchangeType(request.getType())
			.allBanks(sortedRates)
			.build();
	}

	@Override
	public ExchangeQuickResponse calculateExchangeQuick(ExchangeQuickRequest request,
		HttpServletRequest httpServletRequest) {
		return calculateExchangeQuickNormal(request);

	}

	private List<BankRateInfo> sortByExchangeType(List<BankRateInfo> rates, String type) {
		Comparator<BankRateInfo> comparator;

		switch (type) {
			case "SELLCASH":  // 현찰 팔 때 - 낮은 환율이 유리
			case "SEND":      // 송금 보낼 때 - 낮은 환율이 유리
			case "BASE":      // 기준율 - 낮은 순으로 정렬
				comparator = Comparator.comparing(BankRateInfo::getOperator);
				break;

			case "GETCASH":   // 현찰 살 때 - 높은 환율이 유리
			case "RECEIVE":   // 송금 받을 때 - 높은 환율이 유리
				comparator = Comparator.comparing(BankRateInfo::getOperator).reversed();
				break;

			default:
				comparator = Comparator.comparing(BankRateInfo::getOperator);
		}

		return rates.stream()
			.sorted(comparator)
			.collect(Collectors.toList());
	}

	private BankRateInfo calculateBankRate(ExchangeRate rate, BigDecimal amountInKRW) {
		// 환전 후 받게 될 목표 통화 금액 계산
		BigDecimal targetAmount;
		String rateDisplay;

		// KRW -> 목표통화: 원화금액 / 환율
		targetAmount = amountInKRW.divide(rate.getExchangeValue(), 2, RoundingMode.HALF_UP);

		// 환율 표시 (1 목표통화 = X KRW)
		rateDisplay = String.format("1 %s 당 %s KRW",
			rate.getTargetExchange(),
			rateFormatter.format(rate.getExchangeValue()));


		// kb 글자 빼기
		if( rate.getBankName().startsWith("KB")) {
			rate.setBankName(rate.getBankName().substring(2));
		}
		return BankRateInfo.builder()
			.bankName(rate.getBankName())
			.operator(rate.getExchangeValue())
			.exchangeRate(rateDisplay)
			.totalAmount(String.format("%s %s",
				currencyFormatter.format(targetAmount.setScale(2, RoundingMode.HALF_UP)),
				rate.getTargetExchange()))
			.build();
	}

	private ExchangeQuickResponse calculateExchangeQuickNormal(ExchangeQuickRequest request) {

		// 기준 통화(KRW)에서 목표 통화로의 환율 조회
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
