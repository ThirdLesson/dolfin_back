package org.scoula.domain.member.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "아이디 중복 확인 요청")
public record CheckIdRequest(
	@ApiModelProperty(value = "로그인 아이디", example = "scoula123", required = true)
	@NotBlank(message = "아이디를 입력해주세요.")
	String loginId
) {
}
