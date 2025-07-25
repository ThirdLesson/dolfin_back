package org.scoula.domain.codef.dto.request;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "1원 인증에 필요한 객체")
public record CodefVerifyCodeRequest(
	@ApiModelProperty(notes = "인증코드", example = "1234", required = true)
	@NotNull(message = "인증코드는 필수입니다.")
	String authCode
) {
}
