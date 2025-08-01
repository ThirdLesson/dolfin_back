package org.scoula.domain.member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.scoula.domain.member.dto.request.CheckIdRequest;
import org.scoula.domain.member.dto.request.JoinRequest;
import org.scoula.domain.member.dto.request.PhoneNumRequest;
import org.scoula.domain.member.dto.request.PhoneVerificationRequest;
import org.scoula.domain.member.dto.response.JoinResponse;
import org.scoula.domain.member.service.JoinService;
import org.scoula.domain.sms.service.SmsService;
import org.scoula.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

@Api(tags = "회원 가입 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class JoinController {
	private final JoinService joinService;
	private final SmsService smsService;

	@ApiOperation(value = "회원 가입", notes = "사용자 정보를 받아 회원가입을 처리합니다.")
	@PostMapping("/join")
	public SuccessResponse<JoinResponse> joinMember(@RequestBody @Valid JoinRequest joinRequest,
		HttpServletRequest request) throws
		JsonProcessingException {
		JoinResponse joinResponse = joinService.joinMember(joinRequest, request);
		return SuccessResponse.ok(joinResponse);
	}

	@ApiOperation(value = "아이디 중복 확인", notes = "입력한 아이디의 중복 여부를 확인합니다.")
	@PostMapping("/check-id")
	public SuccessResponse<Void> verifyLoginId(@ApiParam(value = "확인할 로그인 아이디")
	@Valid @RequestBody CheckIdRequest checkIdRequest) {
		joinService.checkLoginId(checkIdRequest.loginId());
		return SuccessResponse.ok(null);
	}

	@ApiOperation(value = "휴대폰 인증번호 전송", notes = "회원가입 시 입력한 전화번호로 인증번호를 전송합니다.")
	@PostMapping("/send-code")
	public SuccessResponse<Void> sendSms(
		@RequestBody @Valid PhoneNumRequest phoneNumRequest, HttpServletRequest request) {
		smsService.certificateSMS(phoneNumRequest, request);
		return SuccessResponse.noContent();
	}

	@ApiOperation(value = "휴대폰 인증번호 확인", notes = "입력한 인증번호가 유효한지 확인합니다.")
	@PostMapping("/verify-code")
	public SuccessResponse<Void> verifySms(
		@RequestBody @Valid PhoneVerificationRequest request, HttpServletRequest servletRequest) {
		smsService.verifySMS(request, servletRequest);
		return SuccessResponse.noContent();
	}
}
