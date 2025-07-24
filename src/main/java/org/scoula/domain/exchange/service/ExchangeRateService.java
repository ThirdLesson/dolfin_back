package org.scoula.domain.exchange.service;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.exchange.dto.request.ExchangeBankRequest;
import org.scoula.domain.exchange.dto.request.ExchangeQuickRequest;
import org.scoula.domain.exchange.dto.response.ExchangeBankResponse;
import org.scoula.domain.exchange.dto.response.ExchangeQuickResponse;

public interface ExchangeRateService {

	ExchangeBankResponse calculateExchangeBank(ExchangeBankRequest request, HttpServletRequest httpServletRequest);

	ExchangeQuickResponse calculateExchangeQuick(ExchangeQuickRequest request, HttpServletRequest httpServletRequest);

}
