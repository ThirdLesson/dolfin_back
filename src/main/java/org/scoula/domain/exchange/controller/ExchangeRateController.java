package org.scoula.domain.exchange.controller;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.exchange.dto.request.ExchangeBankRequest;
import org.scoula.domain.exchange.dto.request.ExchangeQuickRequest;
import org.scoula.domain.exchange.dto.response.ExchangeBankResponse;
import org.scoula.domain.exchange.dto.response.ExchangeQuickResponse;
import org.scoula.domain.exchange.service.ExchangeRateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("/exchange")
@Api(tags = "환율 API")
public class ExchangeRateController {

	private final ExchangeRateService exchangeRateService;

	@ApiOperation(
		value = "환율 계산 및 비교",
		notes = "KRW 기준으로 목표 통화에 대한 5개 은행의 환율을 비교하고 최적 환율을 제공합니다."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공적으로 요청이 처리되었습니다.", response = ExchangeBankResponse.class),
		@ApiResponse(code = 400, message = "잘못된 요청입니다."),
		@ApiResponse(code = 500, message = "서버에서 오류가 발생했습니다.")
	})
	@PostMapping("/calculate/bank-rates")
	public ResponseEntity<ExchangeBankResponse> calculateExchangeRateBank(
		@RequestBody ExchangeBankRequest requestBody,
		HttpServletRequest httpServletRequest) {
		ExchangeBankResponse response = exchangeRateService.calculateExchangeBank(requestBody, httpServletRequest);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/calculate/quick-rate")
	public ResponseEntity<ExchangeQuickResponse> calculateExchangeQuickNormal(
		@RequestBody ExchangeQuickRequest requestBody,
		HttpServletRequest httpServletRequest) {
		ExchangeQuickResponse response = exchangeRateService.calculateExchangeQuick(requestBody, httpServletRequest);

		return ResponseEntity.ok(response);
	}

}
