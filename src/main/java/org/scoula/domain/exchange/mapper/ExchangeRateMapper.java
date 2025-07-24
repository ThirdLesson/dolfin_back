package org.scoula.domain.exchange.mapper;

import org.scoula.domain.exchange.entity.ExchangeRate;
import org.apache.ibatis.annotations.Param;


public interface ExchangeRateMapper {


    /**
     * 최신 환율 날짜 조회
     * @return 가장 최근의 환율 날짜
     */
    ExchangeRate findLatestExchangeRate(
        @Param("bankName") String bankName,
        @Param("type") String type,
        @Param("targetCurrency") String targetCurrency
    );



}
