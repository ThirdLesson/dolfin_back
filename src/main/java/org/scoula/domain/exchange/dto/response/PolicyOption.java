package org.scoula.domain.exchange.dto.response;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PolicyOption {
    private String policyName;                  // 정책명
    private BigDecimal exchangeDiscountRate;    // 환전 우대율
    private BigDecimal exchangeCommissionFee;   // 환전 수수료
    private BigDecimal baseTelephoneFee;        // 기본 전화 환율

}
