package org.scoula.domain.codef.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.scoula.domain.codef.dto.request.CodefConnectedIdRequest;
import org.scoula.domain.codef.dto.request.CodefVerifyCodeRequest;
import org.scoula.domain.codef.dto.request.StayExpirationRequest;
import org.scoula.domain.codef.dto.request.WalletRequest;
import org.scoula.domain.codef.dto.response.CodefVerifyCodeResponse;
import org.scoula.domain.codef.dto.response.StayExpirationResponse;
import org.scoula.domain.codef.service.CodefAccountService;
import org.scoula.domain.codef.service.CodefApiClient;
import org.scoula.domain.codef.service.CodefAuthService;
import org.scoula.domain.wallet.service.WalletService;
import org.scoula.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

@Api(tags = "Codef API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/codef")
public class CodefController {

	private final CodefAuthService codefAuthService;
	private final CodefApiClient codefApiClient;
	private final CodefAccountService codefAccountService;
	private final WalletService walletService;

	@ApiOperation(value = "Codef 토큰 발급 API", notes = "Codef 토큰을 발급한 후 레디스에 저장합니다.")
	@PostMapping("/token")
	public SuccessResponse<Void> getCodefToken(HttpServletRequest request) {
		codefAuthService.issueCodefToken(request);
		return SuccessResponse.noContent();
	}

	@ApiOperation(value = "Codef 체류 기간 조회 API", notes = "체류 기간을 조회합니다.")
	@PostMapping("/stay")
	public SuccessResponse<StayExpirationResponse> getCodef(
		@RequestBody StayExpirationRequest stayExpirationRequest, HttpServletRequest request) throws
		JsonProcessingException {
		StayExpirationResponse stayExpiration = codefApiClient.getStayExpiration(stayExpirationRequest, request);
		return SuccessResponse.ok(stayExpiration);
	}

	@ApiOperation(value = "Codef 계좌 등록 및 확인 api", notes = "Codef 사용자 계좌 조회 후 1원을 보냅니다. 인증번호 반환하니 그거 그대로 다시 보내주시면 됩니다.")
	@PostMapping("/account/{memberId}")
	public SuccessResponse<CodefVerifyCodeResponse> associateAccount(
		@RequestBody @Valid CodefConnectedIdRequest codefConnectedIdRequest,
		@PathVariable(name = "memberId") @ApiParam(value = "PathVariable로 유저 id 넘겨주세요") Long memberId,
		HttpServletRequest request) {
		codefAccountService.requestConnectedIdCreate(codefConnectedIdRequest, memberId, request);
		return SuccessResponse.ok(codefAccountService.sendVerifyCode(codefConnectedIdRequest, memberId, request));
	}

	@ApiOperation(value = "Codef 인증번호 확인 api", notes = "1원 인증에 사용된 인증번호를 입력합니다. 만료 시간은 5분입니다.")
	@PostMapping("/account/verify/{memberId}")
	public SuccessResponse<Void> verifyCode(@RequestBody @Valid CodefVerifyCodeRequest codefVerifyCodeRequest,
		@PathVariable(name = "memberId") @ApiParam(value = "PathVariable로 유저 id 넘겨주세요") Long memberId,
		HttpServletRequest request) {
		codefAccountService.verifyCode(codefVerifyCodeRequest, memberId, request);
		return SuccessResponse.noContent();
	}

	@ApiOperation(value = "전자지갑 비밀번호 설정", notes = "전자 지갑 비밀번호를 설정하며, 전자지갑 및 연결 계좌를 생성합니다.")
	@PostMapping("/account/wallet/{memberId}")
	public SuccessResponse<Void> associateAccount(@RequestBody @Valid WalletRequest walletRequest,
		@PathVariable(name = "memberId") @ApiParam(value = "PathVariable로 유저 id 넘겨주세요") Long memberId,
		HttpServletRequest request) {
		walletService.createWallet(walletRequest, memberId, request);
		return SuccessResponse.noContent();
	}

}
