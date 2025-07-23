package org.scoula.domain.member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scoula.domain.member.dto.request.ReissueTokenRequestDto;
import org.scoula.domain.member.dto.request.SignInRequestDto;
import org.scoula.domain.member.dto.response.RefreshResponseDto;
import org.scoula.domain.member.dto.response.SignInResponseDto;
import org.scoula.domain.member.service.MemberAuthService;
import org.scoula.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Api(tags = "인증 API", description = "로그인, 로그아웃, 토큰 재발급 등의 인증 관련 API")
public class AuthController {

	private final MemberAuthService memberAuthService;

	@ApiOperation(value = "로그인", notes = "아이디와 비밀번호를 이용해 로그인을 수행합니다.")
	@ApiResponses({
		@ApiResponse(code = 401, message = "아이디 또는 비밀번호가 일치하지 않습니다.")
	})
	@PostMapping("/signin")
	public SuccessResponse<SignInResponseDto> signIn(@RequestBody SignInRequestDto requestDto,
		HttpServletResponse response, HttpServletRequest request) {
		SignInResponseDto signInResponseDto = memberAuthService.signIn(requestDto, request, response);
		return SuccessResponse.ok(signInResponseDto);
	}

	@ApiOperation(value = "토큰 재발급", notes = "Refresh Token을 이용하여 Access Token을 재발급합니다. "
		+ "로그인한 회원이 뭐든 api를 호출해 사용 중에 토큰 재발급이 필요합니다. 라는 메세지와 401 코드가 반환되면 토큰 현재 refresh api를 호출해 토큰 재발급 시키면 됩니다.\"\n"
		+ "\n 만약 리프레시를 시도했는데, 로그인이 필요한 서비스입니다. 라는 메세지와 400 코드가 반환되면 로그인 페이지로 넘기면 됩니다. ")
	@ApiResponses({
		@ApiResponse(code = 400, message = "로그인이 필요한 서비스입니다.")
	})
	@PostMapping("/refresh")
	public SuccessResponse<RefreshResponseDto> refreshToken(@RequestBody ReissueTokenRequestDto requestDto,
		@ApiParam(value = "Refresh Token (쿠키)", required = true)
		@CookieValue(name = "refreshToken", required = false) String refreshToken,
		HttpServletRequest request) {
		return SuccessResponse.ok(memberAuthService.reissueToken(requestDto, refreshToken, request));
	}

	@ApiOperation(value = "로그아웃", notes = "클라이언트에서 보유한 Refresh Token을 만료시키고 로그아웃합니다.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "로그아웃 성공")
	})
	@GetMapping("/signout")
	public SuccessResponse<Void> signOut(HttpServletResponse response) {
		memberAuthService.signOut(response);
		return SuccessResponse.noContent();
	}
}
