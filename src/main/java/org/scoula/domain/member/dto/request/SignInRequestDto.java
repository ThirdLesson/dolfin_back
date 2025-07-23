package org.scoula.domain.member.dto.request;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public record SignInRequestDto(
	@ApiModelProperty(value = "회원 로그인 아이디", example = "ss7622")
	@NotNull(message = "아이디는 필수 입력값입니다.")
	String loginId,
	@ApiModelProperty(value = "회원 비밀번호", example = "password")
	@NotNull(message = "비밀번호는 필수 입력값입니다.")
	String password
) {
}
