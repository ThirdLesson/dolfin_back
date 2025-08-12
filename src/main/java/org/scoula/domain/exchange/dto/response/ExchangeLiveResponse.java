package org.scoula.domain.exchange.dto.response;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@ApiModel(description = "실시간 환율 응답 DTO")
public class ExchangeLiveResponse {
	@ApiModelProperty(value = "환율 통황", example = "USD")
	private String currency;
	@ApiModelProperty(value = "환율 값", example = "1300.50")
	private BigDecimal exchangeRate;

}
