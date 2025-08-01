package org.scoula.domain.exchange.dto.response.exchangeResponse;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel(description = "정책 응답 정보")
public class PolicyResponse {
	@ApiModelProperty(value = "정책명", example = "KB WELCOME PACKAGE (환율 50% 우대)")
	String name;                  // 정책명
	@ApiModelProperty(value = "정책 적용시 금액", example = "1356.5 USD")
	String amount;            // 정책 적용 금액
}
