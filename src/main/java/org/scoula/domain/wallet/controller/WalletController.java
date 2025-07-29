package org.scoula.domain.wallet.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.scoula.domain.wallet.dto.request.ChargeWalletRequest;
import org.scoula.domain.wallet.dto.response.WalletResponse;
import org.scoula.domain.wallet.service.WalletService;
import org.scoula.global.response.SuccessResponse;
import org.scoula.global.security.dto.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

@Api(tags = "지갑 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/wallet")
public class WalletController {

	private final WalletService walletService;

	@ApiOperation(value = "전자지갑 조회 api", notes = "유저의 전자지갑을 조회합니다.")
	@GetMapping
	public SuccessResponse<WalletResponse> findWallet(
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		return SuccessResponse.ok(walletService.getWalletByMember(customUserDetails.getMember()));
	}

	@ApiOperation(value = "전자지갑에 포인트 충전 api", notes = "전자지갑에 포인트를 충전합니다.")
	@PostMapping("/{walletId}")
	public SuccessResponse<Void> chargePointToWallet(
		@PathVariable("walletId") @ApiParam(value = "PathVariable로 전자지갑 id 넘겨주세요") Long walletId,
		@RequestBody @Valid ChargeWalletRequest chargeWalletRequest,
		HttpServletRequest request) {
		walletService.chargeWallet(chargeWalletRequest, walletId, request);
		return SuccessResponse.noContent();
	}
}
