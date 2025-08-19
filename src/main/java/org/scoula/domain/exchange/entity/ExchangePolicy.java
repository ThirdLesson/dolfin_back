package org.scoula.domain.exchange.entity;

import java.math.BigDecimal;

import org.scoula.global.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangePolicy extends BaseEntity {
    private Long policyId;                     
    private String bankName;                    
    private String targetCurrency;             
    private Type exchangeType;                  
    private String policyName;                 
    private String description;                 
    private BigDecimal exchangeDiscountRate;   
    private BigDecimal exchangeCommissionFee;   
    private BigDecimal baseTelegraphFee;       
    private Boolean isActive;                   
}
