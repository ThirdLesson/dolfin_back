package org.scoula.domain.remittancegroup.dto.request;

import javax.validation.constraints.NotNull;

import org.scoula.global.constants.Currency;

import io.swagger.annotations.ApiModelProperty;

public record RemittanceGroupMemberCountRequest(
	@ApiModelProperty(
		value = "조회 통화",
		example = "USD",
		required = true
	)
	@NotNull(message = "통화는 필수로 입력하셔야 합니다.")
	Currency currency
) {
}
