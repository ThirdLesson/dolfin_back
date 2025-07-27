package org.scoula.domain.exchange.mapper;

import java.util.List;

import org.scoula.domain.exchange.entity.ExchangeMonthly;

public interface ExchangeMonthlyMapper {

	List<ExchangeMonthly> findLatestExchangeMonthly(
		String targetExchange
	);


}
