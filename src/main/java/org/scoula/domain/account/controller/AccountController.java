package org.scoula.domain.account.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.account.dto.response.AccountListResponse;
import org.scoula.domain.account.service.AccountService;
import org.scoula.domain.exchange.dto.response.exchangeResponse.ExchangeBankResponse;
import org.scoula.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/account")
@Api(tags = "계좌 관련 API (조회)")
public class AccountController {

	private final AccountService accountService;

	@ApiOperation(
		value = "전자지갑과 연결된 계좌 조회",
		notes = "전자지갑 아이디를 기준으로 연결된 계좌 리스트를 조회합니다."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK"),
		@ApiResponse(code = 400, message = "해당 ID의 wallet이 존재하지 않습니다."),
		@ApiResponse(code = 500, message = "서버에서 오류가 발생했습니다.")
	})
	@GetMapping("/{walletId}")
	public SuccessResponse<List<AccountListResponse>> getAllAccountsByWalletId(
		@PathVariable(name = "walletId") @ApiParam(value = "PathVariable로 전자지갑 Id를 제공해주세요", required = true) Long walletId,
		HttpServletRequest request) {
		return SuccessResponse.ok(accountService.getAllAccountsByWalletId(walletId, request));
	}
}
