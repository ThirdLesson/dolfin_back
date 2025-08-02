package org.scoula.domain.exchange.dto.response.exchangeResponse;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Data
@Builder
@ApiModel(
	description = "환율 계산 응답 정보",
	value = "ExchangeBankResponse"
)
@JsonPropertyOrder({"requestedAmount", "targetCurrency", "exchangeType", "banks"})
public class ExchangeBankResponse {

	@ApiModelProperty(
		value = "요청한 원화 금액",
		example = "2000000",
		notes = "사용자가 입력한 원화 금액 (단위: 원)",
		required = true,
		position = 0

	)
	private BigDecimal requestedAmount;

	@ApiModelProperty(
		value = "목표 통화 코드",
		example = "USD",
		notes = "ISO 4217 통화 코드 (USD, EUR, JPY 등)",
		required = true,
		position = 1
	)
	private String targetCurrency;

	@ApiModelProperty(
		value = "환율 타입",
		example = "SEND",
		notes = "SEND(해외송금), GETCASH(현찰구매), RECEIVE(해외수취), SELLCASH(현찰판매)",
		required = true,
		allowableValues = "SEND,GETCASH,RECEIVE,SELLCASH",
		position = 2

	)
	private String exchangeType;

	@ApiModelProperty(
		value = "은행별 환율 정보 리스트",
		notes = "5개 주요 은행의 환율 정보를 유리한 순서로 정렬하여 제공합니다. " +
			"각 은행별로 기본 환율과 우대정책이 적용된 환율을 모두 포함합니다.",
		required = true,
		position = 3
	)
	private List<BankRateInfo> banks;

}
