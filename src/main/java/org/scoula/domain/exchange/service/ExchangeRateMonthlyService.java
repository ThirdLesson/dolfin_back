package org.scoula.domain.exchange.service;

import static java.util.stream.Collectors.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.exchange.dto.response.ExchangeMonthlyResponse;
import org.scoula.domain.exchange.entity.ExchangeMonthly;
import org.scoula.domain.exchange.exception.ExchangeErrorCode;
import org.scoula.domain.exchange.mapper.ExchangeMonthlyMapper;
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
public class ExchangeRateMonthlyService {

	private final ExchangeMonthlyMapper exchangeMonthlyMapper;

	public List<ExchangeMonthlyResponse> findLatestExchangeMonthly(HttpServletRequest httpServletRequest,
		String targetExchange) {
		List<ExchangeMonthly> exchangeMonthlies = exchangeMonthlyMapper.findLatestExchangeMonthly(targetExchange);
		if (exchangeMonthlies.isEmpty()) {
			throw new CustomException(
				ExchangeErrorCode.EXCHANGE_NOT_FOUND, LogLevel.ERROR,
				httpServletRequest.getHeader("txId"),
				Common.builder()
					.srcIp(httpServletRequest.getRemoteAddr())
					.callApiPath(httpServletRequest.getRequestURI())
					.deviceInfo(httpServletRequest.getHeader("user-agent"))
					.retryCount(0)
					.build());
		}

		return exchangeMonthlies.stream()
			.map(this::toResponse)
			.collect(toList());
	}

	public ExchangeMonthlyResponse toResponse(ExchangeMonthly exchangeMonthly) {
		return ExchangeMonthlyResponse.builder()
			.targetExchange(exchangeMonthly.getTargetExchange())
			.exchangeValue(exchangeMonthly.getExchangeValue())
			.exchangeDate(exchangeMonthly.getExchangeDate())
			.build();
	}
}
