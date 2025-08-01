package org.scoula.domain.exchange.controller;
// ExchangeController.java

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.scoula.domain.exchange.dto.request.ExchangeBankRequest;
import org.scoula.domain.exchange.dto.response.exchangeResponse.ExchangeBankResponse;
import org.scoula.domain.exchange.service.finalService.ExchangeFinalService;
import org.scoula.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/exchange/v2")
@RequiredArgsConstructor
@Api(tags = "환율 계산 API V2", description = "환율 계산 및 정책 적용 API")
public class ExchangePolicyController {

	private final ExchangeFinalService exchangeFinalService;

	/**
	 * 환율 계산 API
	 *
	 * @param request            환율 계산 요청
	 * @param httpServletRequest HTTP 요청 정보
	 * @return 은행별 환율 정보 (기본 환율 + 정책 옵션)
	 */
	@PostMapping("/check")
	@ApiOperation(
		value = "환율 계산",
		notes = "지정된 금액에 대한 5개 은행의 환율 정보를 계산합니다. 기본 환율과 각 은행의 우대 정책이 포함됩니다."
	)
	public SuccessResponse<ExchangeBankResponse> calculateExchange(
		@ApiParam(value = "환율 계산 요청 정보", required = true)
		@Valid @RequestBody ExchangeBankRequest request,
		HttpServletRequest httpServletRequest) {

		log.info("환율 계산 요청 - 통화: {}, 금액: {}, 타입: {}, IP: {}",
			request.getTargetCurrency(), request.getAmount(), request.getType(),
			httpServletRequest.getRemoteAddr());

		// 최종 환율 계산
		ExchangeBankResponse response = exchangeFinalService.calculateFinalExchangeRate(request,
			httpServletRequest);

		log.info("환율 계산 성공 - 처리된 은행 수: {}", response.getBanks().size());

		return SuccessResponse.ok(response);

	}


}
