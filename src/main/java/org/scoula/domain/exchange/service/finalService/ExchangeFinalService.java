package org.scoula.domain.exchange.service.finalService;


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
	
	public ExchangeBankResponse calculateFinalExchangeRate(ExchangeBankRequest request,
		HttpServletRequest httpServletRequest) {

		ExchangeInformation exchangeinformation = exchangeCommissionService.getExchangeCommissionFee("하나은행",
			request.getTargetCurrency(),
			request.getType(),
			request.getAmount());

		ExchangeBankResponse bankResponse = exchangeRateService.calculateExchangeBank(
			request, exchangeinformation.getUsdAmount(),exchangeinformation.exchangeCommissionFee);

		exchangePolicyService.getAllBanksWithPolicy(bankResponse, exchangeinformation.usdAmount, exchangeinformation.getExchangeCommissionFee());

		return bankResponse;
	}




}
