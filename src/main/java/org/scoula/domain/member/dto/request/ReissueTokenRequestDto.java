package org.scoula.domain.member.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;

public record ReissueTokenRequestDto(
	@ApiModelProperty(value = "회원 고유 id", example = "1")
	@NotBlank(message = "멤버 고유 아이디는 필수 입니다.")
	Long memberId,

	@ApiModelProperty(value = "회원 로그인 아이디", example = "ss7622")
	@NotBlank(message = "로그인 아이디는 필수 입니다.")
	String loginId
) {
}
