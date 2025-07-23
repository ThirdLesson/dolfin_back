package org.scoula.domain.exchange.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@ApiModel(description = "환율 계산 응답 정보")
public class ExchangeBankResponse {
    
    @ApiModelProperty(value = "요청한 원화 금액", example = "1000000")
    private BigDecimal requestedAmountKRW;
    
    @ApiModelProperty(value = "목표 통화 코드", example = "VND")
    private String targetCurrency;
    
    @ApiModelProperty(value = "환율 타입", example = "SELLCASH")
    private String exchangeType;

    @ApiModelProperty(value = "모든 은행의 환율 정보 (환율이 유리한 순서로 정렬)")
    private List<BankRateInfo> allBanks;

}
