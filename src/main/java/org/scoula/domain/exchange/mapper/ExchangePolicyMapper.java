package org.scoula.domain.exchange.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.exchange.entity.ExchangePolicy;

@Mapper
public interface ExchangePolicyMapper {

    List<ExchangePolicy> findAllPolicyByCond(@Param("bankName") String bankName,
        @Param("targetCurrency") String targetCurrency,
        @Param("exchangeType") String exchangeType);

}


