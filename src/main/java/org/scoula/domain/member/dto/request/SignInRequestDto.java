package org.scoula.domain.member.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;

public record SignInRequestDto(
	@ApiModelProperty(value = "회원 로그인 아이디", example = "ss7622")
	@NotBlank(message = "아이디는 필수 입력값입니다.")
	String loginId,
	@ApiModelProperty(value = "회원 비밀번호", example = "password")
	@NotBlank(message = "비밀번호는 필수 입력값입니다.")
	String password
) {
}
