package org.scoula.domain.wallet.controller;

import org.scoula.domain.wallet.dto.response.WalletResponse;
import org.scoula.domain.wallet.service.WalletService;
import org.scoula.global.response.SuccessResponse;
import org.scoula.global.security.dto.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
}
