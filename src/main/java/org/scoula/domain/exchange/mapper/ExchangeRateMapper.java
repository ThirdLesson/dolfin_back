package org.scoula.domain.exchange.mapper;

import java.util.List;

import org.scoula.domain.exchange.dto.response.ExchangeLiveResponse;
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


    /**
     * 특정 은행의 특정 통화 환율 조회
     */
    ExchangeRate findByBankAndTargetCurrencyAndType(
        @Param("bankName") String bankName,
        @Param("targetCurrency") String targetCurrency,
        @Param("type") String type
    );

    /**
     * 최신 환율 조회 (가장 최근 날짜)
     */
    List<ExchangeRate> findLatestRates(
        @Param("type") String type
    );

    /**
     * usd 환율 조회
     *
     */
    ExchangeRate findUsdExchangeRate(
        @Param("bankName") String bankName
    );

    /**
     * 실시간 환율 조회
     * @return 실시간 환율 리스트
     */
    List<ExchangeLiveResponse> getExchangeLiveList();
}
