package org.scoula.domain.exchange.service.finalService;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ExchangeInformation{
		BigDecimal exchangeCommissionFee;
		BigDecimal usdAmount;
	}
