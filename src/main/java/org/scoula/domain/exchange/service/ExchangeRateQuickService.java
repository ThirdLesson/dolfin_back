package org.scoula.domain.exchange.service;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.exchange.dto.request.ExchangeBankRequest;
import org.scoula.domain.exchange.dto.request.ExchangeQuickRequest;
import org.scoula.domain.exchange.dto.response.ExchangeLiveResponse;
import org.scoula.domain.exchange.dto.response.exchangeResponse.ExchangeBankResponse;
import org.scoula.domain.exchange.dto.response.ExchangeQuickResponse;

public interface ExchangeRateQuickService {

	ExchangeBankResponse calculateExchangeBank(ExchangeBankRequest request, HttpServletRequest httpServletRequest);

	ExchangeBankResponse calculateExchangeBankV2(ExchangeBankRequest request, HttpServletRequest httpServletRequest);

	ExchangeQuickResponse calculateExchangeQuick(ExchangeQuickRequest request, HttpServletRequest httpServletRequest);

	ExchangeLiveResponse getLatestExchangeRate(HttpServletRequest request,String upperCase);
}
