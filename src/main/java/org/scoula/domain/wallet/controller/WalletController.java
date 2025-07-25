package org.scoula.domain.wallet.controller;

import org.scoula.domain.wallet.dto.response.WalletResponse;
import org.scoula.domain.wallet.service.WalletService;
import org.scoula.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wallet")
public class WalletController {

	private final WalletService walletService;

	@ApiOperation(value = "전자지갑 조회 api", notes = "유저 아이디를 기반으로 전자지갑을 조회합니다.")
	@GetMapping("/{memberId}")
	public SuccessResponse<WalletResponse> findWalletByMemberId(
		@PathVariable("memberId") @ApiParam(value = "PathVariable로 유저 id 넘겨주세요") Long memberId) {
		return SuccessResponse.ok(walletService.getWalletByMemberId(memberId));
	}
}
