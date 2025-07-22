package org.scoula.domain.member.dto.request;

import io.swagger.annotations.ApiModelProperty;

public record SignInRequestDto(
	@ApiModelProperty(value = "회원 로그인 아이디", example = "ss7622")
	String loginId,
	@ApiModelProperty(value = "회원 비밀번호", example = "password")
	String password
) {
}
