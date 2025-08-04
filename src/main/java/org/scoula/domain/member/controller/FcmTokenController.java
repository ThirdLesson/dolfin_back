package org.scoula.domain.member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.scoula.domain.member.dto.request.FcmTokenRequest;
import org.scoula.domain.member.service.MemberService;
import org.scoula.global.response.SuccessResponse;
import org.scoula.global.security.dto.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
@Api(tags = "fcm 토큰 저장", description = "fcm 토큰 저장 api")
public class FcmTokenController {

	private final MemberService memberService;

	@PostMapping
	@ApiOperation(value = "fcm 토큰 저장", notes = "fcm 토큰 저장 api.")
	public SuccessResponse<Void> saveFcmToken(@RequestBody @Valid FcmTokenRequest fcmTokenRequest,
		@AuthenticationPrincipal CustomUserDetails customUserDetails,
		HttpServletRequest request) {
		memberService.updateFcmToken(customUserDetails.getMember().getMemberId(), fcmTokenRequest.fcmToken());
		return SuccessResponse.noContent();
	}
}
