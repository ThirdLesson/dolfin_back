package org.scoula.domain.exchange.dto.response.exchangeResponse;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel(
	description = "우대정책 적용 결과",
	value = "PolicyResponse"
)public class PolicyResponse {
	@ApiModelProperty(
		value = "정책명",
		example = "KB WELCOME PACKAGE (환율 50% 우대)",
		notes = "은행에서 제공하는 우대정책의 정식 명칭",
		required = true,
		position = 1
	)
	String name;                  // 정책명

	@JsonIgnore  // 이 어노테이션으로 JSON 응답에서 제외
	BigDecimal numericAmount;

	@ApiModelProperty(
		value = "정책 적용시 최종 금액",
		example = "1,403.58 USD",
		notes = "해당 우대정책을 적용했을 때 실제로 받게 되는 금액 (통화 단위 포함). " +
			"기본 환율 대비 얼마나 더 유리한지 비교할 수 있습니다.",
		required = true,
		position = 2
	)	String amount;            // 정책 적용 금액
}
