package org.scoula.domain.member.dto.request;

import io.swagger.annotations.ApiOperation;

@ApiOperation("sms 인증 요청 확인 객체")
public record PhoneVerificationRequest(
	String phoneNumber,
	String code
) {
}
