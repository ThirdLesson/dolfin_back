package org.scoula.domain.exchange.dto.response;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PolicyOption {
    private String policyName;                  
    private BigDecimal exchangeDiscountRate;    
    private BigDecimal exchangeCommissionFee;   
    private BigDecimal baseTelephoneFee;       

}
