package org.scoula.domain.member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.scoula.domain.member.dto.request.JoinRequest;
import org.scoula.domain.member.service.JoinService;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class JoinController {
	private final JoinService joinService;

	@ApiOperation(value = "회원 가입", notes = "사용자 정보를 받아 회원가입을 처리합니다.")
	@PostMapping("/auth/join")
	public SuccessResponse<Void> joinMember(@RequestBody @Valid JoinRequest joinRequest,
		HttpServletRequest request) throws
		JsonProcessingException {
		joinService.joinMember(joinRequest, request);
		return SuccessResponse.noContent();
	}

	@ApiOperation(value = "아이디 중복 확인", notes = "입력한 아이디의 중복 여부를 확인합니다.")
	@PostMapping("/check-id")
	public SuccessResponse<Void> verifyLoginId(@ApiParam(value = "확인할 로그인 아이디")
	@RequestBody @NotBlank(message = "아이디를 입력해주세요.") String loginId) {
		joinService.checkLoginId(loginId);
		return SuccessResponse.ok(null);
	}

	// TODO 핸드폰 인증

}
