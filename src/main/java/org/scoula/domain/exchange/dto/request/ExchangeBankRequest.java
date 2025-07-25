package org.scoula.domain.exchange.dto.request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
@ApiModel(description = "환율 계산 요청 정보")
public class ExchangeBankRequest {

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

	@ApiModelProperty(
		value = "환율 타입",
		example = "GETCASH",
		required = true,
		allowableValues = "GETCASH, SELLCASH, SEND, RECEIVE, BASE",
		position = 3
	)
	private String type;
}
