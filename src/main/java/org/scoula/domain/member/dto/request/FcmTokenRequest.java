package org.scoula.domain.member.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;

public record FcmTokenRequest(
	@NotBlank(message = "fcm 토큰은 필수로 넣어주셔야 합니다.")
	@ApiModelProperty(value = "fcm 토큰", example = "발급받은 fcm 토큰 값", required = true)
	String fcmToken
) {
}
