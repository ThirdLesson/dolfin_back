package org.scoula.domain.exchange.mapper;

import java.util.List;

import org.scoula.domain.exchange.dto.response.ExchangeLiveResponse;
import org.scoula.domain.exchange.entity.ExchangeRate;
import org.apache.ibatis.annotations.Param;


public interface ExchangeRateMapper {


  
    ExchangeRate findLatestExchangeRate(
        @Param("bankName") String bankName,
        @Param("type") String type,
        @Param("targetCurrency") String targetCurrency
    );


   
    ExchangeRate findByBankAndTargetCurrencyAndType(
        @Param("bankName") String bankName,
        @Param("targetCurrency") String targetCurrency,
        @Param("type") String type
    );

   
    List<ExchangeRate> findLatestRates(
        @Param("type") String type
    );

  
    ExchangeRate findUsdExchangeRate(
        @Param("bankName") String bankName
    );

   
    List<ExchangeLiveResponse> getExchangeLiveList();
}
