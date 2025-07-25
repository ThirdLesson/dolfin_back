package org.scoula.domain.exchange.dto.response;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel(description = "환율 계산 응답 정보")
public class ExchangeQuickResponse {
    
    @ApiModelProperty(value = "요청한 원화 금액", example = "1000000")
    private BigDecimal requestedAmount;

    @ApiModelProperty(value = "목표 통화 코드", example = "VND")
    private String targetCurrency;

    @ApiModelProperty(value = "5.3 VND(100)", example = "5.3 VND")
    private String ResultRateDisplay;

}
