package org.scoula.domain.exchange.service;

import static org.scoula.domain.member.exception.MemberErrorCode.*;
import static org.scoula.domain.wallet.exception.WalletErrorCode.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.account.exception.AccountErrorCode;
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
			.banks(sortedRates)
			.build();
	}


	private List<BankRateInfo> sortByExchangeType(List<BankRateInfo> rates, String type) {
		Comparator<BankRateInfo> comparator;

		switch (type) {
			case "SELLCASH":  // 현찰 팔 때 - 낮은 환율이 유리
			case "SEND":      // 송금 보낼 때 - 낮은 환율이 유리
			case "BASE":      // 기준율 - 낮은 순으로 정렬
				comparator = Comparator.comparing(BankRateInfo::getTargetoperation);
				break;

			case "GETCASH":   // 현찰 살 때 - 높은 환율이 유리
			case "RECEIVE":   // 송금 받을 때 - 높은 환율이 유리
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
		// 환전 후 받게 될 목표 통화 금액 계산
		BigDecimal targetAmount;
		String rateDisplay;

		// KRW -> 목표통화: 원화금액 / 환율
		targetAmount = amountInKRW.divide(rate.getExchangeValue(), 2, RoundingMode.HALF_UP);

		// 환율 표시 (1 목표통화 = X KRW)
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

		// 각 은행별 환율 정보 조회
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

	/**
	 * 기본 정책 옵션 계산 (0% 우대율)
	 */
	private BankRateInfo calculatePolicyOption(ExchangeRate targetExchange, ExchangeRate baseExchange,
		BigDecimal amountInKRW) {
		BigDecimal transferFee = BigDecimal.valueOf(8000); // 전신료
		BigDecimal zeroPreferentialRate = BigDecimal.ZERO; // 0% 우대율 (일반 요율)

		// 1. 전신료 차감
		BigDecimal amountAfterTransferFee = amountInKRW.subtract(transferFee);

		// 2. 일반 요율 (0% 우대) 계산 - 실제로는 targetExchange(SEND) 환율 그대로 사용
		BigDecimal baseActualRate = targetExchange.getExchangeValue(); // SEND 타입 환율 (0% 우대)
		BigDecimal baseFinalAmount = amountAfterTransferFee.divide(baseActualRate, 2, RoundingMode.HALF_UP);

		// 3. 환율 표시
		String rateDisplay = String.format("1 %s 당 %s KRW",
			targetExchange.getTargetExchange(),
			rateFormatter.format(baseActualRate));

		// 4. 일반 요율 결과
		String totalAmountDisplay = String.format("%s %s",
			currencyFormatter.format(baseFinalAmount.setScale(2, RoundingMode.HALF_UP)),
			targetExchange.getTargetExchange());

		return BankRateInfo.builder()
			.bankName(targetExchange.getBankName())
			.targetoperation(baseActualRate)  // 0% 우대 적용된 환율 (실제로는 SEND 그대로)
			.exchangeRate(rateDisplay)
			.totalAmount(totalAmountDisplay)
			.policyList(new ArrayList<>()) // 나중에 addPolicyOption으로 추가
			.build();
	}


	@Override
	public ExchangeQuickResponse calculateExchangeQuick(ExchangeQuickRequest request,
		HttpServletRequest httpServletRequest) {
		return calculateExchangeQuickNormal(request);

	}

	@Override
	public ExchangeLiveResponse getLatestExchangeRate(HttpServletRequest request, String upperCase) {

		// 기준 통화(KRW)에서 목표 통화로의 환율 조회
		ExchangeRate baseRate = exchangeRateMapper.findLatestExchangeRate("하나은행", "BASE", upperCase);

		if (baseRate == null) {
			throw new CustomException(ExchangeErrorCode.EXCHANGE_NOT_FOUND, LogLevel.WARNING, null,
				Common.builder().deviceInfo(request.getHeader("host-agent"))
					.srcIp(request.getRemoteAddr())
					.callApiPath(request.getRequestURI())
					.apiMethod(request.getMethod())
					.deviceInfo(request.getHeader("user-agent"))
					.build());
		}

		BigDecimal exchangeValue = baseRate.getExchangeValue();
		String formattedRate = rateFormatter.format(exchangeValue);

		if (upperCase.equals("JPY") || upperCase.equals("VND")) {
			formattedRate = String.format("%s %s (100)", formattedRate, upperCase);
		} else {
			formattedRate = String.format("%s %s", formattedRate, upperCase);
		}
		return new ExchangeLiveResponse(formattedRate);
	}

	/**
	 * 빠른 환율 계산
	 * 기준 통화(KRW)에서 목표 통화로의 환율을 조회하여, 요청한 금액에 대한 환전 후 금액을 계산합니다.
	 * 환율 타입은 BASE로 고정되어 있습니다.
	 * @param request 환율 계산 요청 정보
	 * @return ExchangeQuickResponse
	 */
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
