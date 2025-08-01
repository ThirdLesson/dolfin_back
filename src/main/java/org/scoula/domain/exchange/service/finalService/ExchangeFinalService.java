package org.scoula.domain.exchange.service.finalService;

// ExchangeFinalService.java
import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.exchange.dto.request.ExchangeBankRequest;
import org.scoula.domain.exchange.dto.response.exchangeResponse.ExchangeBankResponse;
import org.scoula.domain.exchange.service.policy.ExchangePolicyService;
import org.scoula.domain.exchange.service.policy.ExchangeRateService;
import org.scoula.domain.exchange.service.policy.ExchangeCommissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExchangeFinalService {

	private final ExchangePolicyService exchangePolicyService;
	private final ExchangeRateService exchangeRateService;
	private final ExchangeCommissionService exchangeCommissionService;
	/**
	 * 최종 환율 계산 서비스
	 *
	 * @param request            환율 계산 요청 정보
	 * @param httpServletRequest HTTP 요청 정보
	 * @return 최종 환율 정보 (기본 환율 + 정책 옵션)
	 */
	public ExchangeBankResponse calculateFinalExchangeRate(ExchangeBankRequest request,
		HttpServletRequest httpServletRequest) {

		// 1. 송금 수수료 적용
		BigDecimal exchangeCommissionFee = exchangeCommissionService.getExchangeCommissionFee("하나은행", request.getType(), request.getAmount());

		// 2. 기본 환율 계산 (5개 은행, 0% 우대율, 수수료 포함)
		ExchangeBankResponse bankResponse = exchangeRateService.calculateExchangeBank(
			request, exchangeCommissionFee, httpServletRequest);

		// 3. 정책 적용 (향후 구현)
		exchangePolicyService.getAllBanksWithPolicy(bankResponse, exchangeCommissionFee);
		return bankResponse;
	}




}
