package org.scoula.domain.exchange.dto.request;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "환율 계산 요청 정보")
public class ExchangeQuickRequest {

	@ApiModelProperty(
		value = "환전하려는 원화(KRW) 금액",
		example = "1000000",
		required = true,
		position = 1
	)
	private BigDecimal amount;

	@ApiModelProperty(
		value = "목표 통화 코드",
		example = "VND",
		required = true,
		allowableValues = "USD, JPY, EUR, GBP, CAD, HKD, CNY, THB, IDR, VND, RUB, MYR",
		position = 2
	)
	private String targetCurrency;

}
