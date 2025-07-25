package org.scoula.domain.member.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("sms 인증 요청 확인 객체")
public record PhoneVerificationRequest(
	@ApiModelProperty(value = "전화번호 입력", example = "01012341234", required = true)
	@NotBlank(message = "전화번호를 입력해주세요.")
	String phoneNumber,

	@ApiModelProperty(value = "코드 입력", example = "1234", required = true)
	@NotBlank(message = "코드를 입력해주세요.")
	String code
) {
}
