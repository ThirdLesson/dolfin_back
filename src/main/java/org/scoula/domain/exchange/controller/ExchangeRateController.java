package org.scoula.domain.exchange.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.exchange.dto.request.ExchangeBankRequest;
import org.scoula.domain.exchange.dto.request.ExchangeQuickRequest;
import org.scoula.domain.exchange.dto.response.exchangeResponse.ExchangeBankResponse;
import org.scoula.domain.exchange.dto.response.ExchangeMonthlyResponse;
import org.scoula.domain.exchange.dto.response.ExchangeQuickResponse;
import org.scoula.domain.exchange.service.ExchangeRateMonthlyService;
import org.scoula.domain.exchange.service.ExchangeRateQuickService;
import org.scoula.domain.exchange.service.finalService.ExchangeFinalService;
import org.scoula.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/exchange")
@Api(tags = "환율 API")
public class ExchangeRateController {

	private final ExchangeRateQuickService exchangeRateQuickService;
	private final ExchangeRateMonthlyService exchangeRateMonthlyService;
	private final ExchangeFinalService exchangeFinalService;

	@ApiOperation(
		value = "환율 계산 및 우대정책 비교",
		notes = "원화 기준으로 5개 주요 은행(국민, 하나, 신한, 우리, 기업)의 환율을 비교합니다."
	)
	@PostMapping("/check")
	public SuccessResponse<ExchangeBankResponse> calculateExchangeRateBank(
		@RequestBody ExchangeBankRequest requestBody,
		HttpServletRequest httpServletRequest) {
		ExchangeBankResponse response = exchangeFinalService.calculateFinalExchangeRate(
			requestBody, httpServletRequest);
		return SuccessResponse.ok(response);
	}

	@ApiOperation(
		value = "환율 빠른 계산",
		notes = "KRW 기준으로 목표 통화에 대한 환율 1개를 빠르게 계산합니다. "
	)
	@PostMapping("/calculate")
	public SuccessResponse<ExchangeQuickResponse> calculateExchangeQuickNormal(
		@RequestBody ExchangeQuickRequest requestBody,
		HttpServletRequest httpServletRequest) {
		ExchangeQuickResponse response = exchangeRateQuickService.calculateExchangeQuick(requestBody, httpServletRequest);

		return SuccessResponse.ok(response);
	}

	@ApiOperation(
		value = "환율 월 그래프",
		notes = "특정 통화에 대한 최근 1개월간의 환율 데이터를 조회합니다."
	)
	@GetMapping("/graph")
	public SuccessResponse<List<ExchangeMonthlyResponse>> calculateExchangeMonthlyGraph(
		HttpServletRequest httpServletRequest,
		@ApiParam(value = "PathVariable로 변화 대상 통화명을 입력해주세요 (예 : USD) ", required = true)
		@RequestParam String targetExchange) {
		List<ExchangeMonthlyResponse> latestExchangeMonthly = exchangeRateMonthlyService.findLatestExchangeMonthly(
			httpServletRequest, targetExchange);
		return SuccessResponse.ok(
			latestExchangeMonthly
		);
	}



}
