package org.scoula.domain.member.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

@ApiOperation("sms 인증 요청 객체")
public record PhoneNumRequest(
	@ApiModelProperty(value = "전화번호 입력", required = true)
	@NotBlank
	String phoneNumber
) {
}
