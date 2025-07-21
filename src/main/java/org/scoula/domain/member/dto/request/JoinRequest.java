package org.scoula.domain.member.dto.request;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "회원가입 요청 데이터")
public record JoinRequest(
	@ApiModelProperty(value = "로그인 아이디", example = "user123", required = true)
	@NotBlank(message = "로그인 아이디는 필수 입력 값입니다.")
	String loginId,

	@ApiModelProperty(value = "비밀번호", example = "password123!", required = true)
	@NotBlank(message = "비밀번호는 필수 입력 값입니다.")
	String password,

	@ApiModelProperty(value = "휴대폰 번호", example = "010-1234-5678", required = true)
	@NotBlank(message = "휴대폰 번호는 필수 입력 값입니다.")
	String phoneNumber,

	@ApiModelProperty(value = "이름", example = "홍길동", required = true)
	@NotBlank(message = "이름은 필수 입력 값입니다.")
	String name,

	@ApiModelProperty(value = "생년월일", example = "2000-01-01", required = true)
	@NotNull(message = "생년월일은 필수 입력 값입니다.")
	LocalDate birth,

	@ApiModelProperty(value = "여권 번호", example = "M12345678", required = true)
	@NotBlank(message = "여권 번호는 필수 입력 값입니다.")
	String passportNumber,

	@ApiModelProperty(value = "국적", example = "KOR", required = true)
	@NotBlank(message = "국적은 필수 입력 값입니다.")
	String nationality,

	@ApiModelProperty(value = "체류 만료일", example = "2026-12-31", required = true)
	@NotNull(message = "체류 만료일은 필수 입력 값입니다.")
	LocalDate remainTime
) {
}
