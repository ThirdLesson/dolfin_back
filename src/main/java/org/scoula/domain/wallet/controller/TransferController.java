package org.scoula.domain.wallet.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.scoula.domain.codef.service.CodefApiClient;
import org.scoula.domain.wallet.dto.request.AccountDepositorRequest;
import org.scoula.domain.wallet.dto.request.TransferToAccountRequest;
import org.scoula.domain.wallet.dto.request.TransferToWalletRequest;
import org.scoula.domain.wallet.dto.response.DepositorResponse;
import org.scoula.domain.wallet.service.WalletService;
import org.scoula.global.response.SuccessResponse;
import org.scoula.global.security.dto.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "송금 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/transfer")
public class TransferController {

	private final WalletService walletService;
	private final CodefApiClient codefApiClient;

	@ApiOperation(value = "전화번호로 예금주명 확인 API", notes = "전화번호를 통해 예금주명을 확인합니다.")
	@GetMapping("/phone-num")
	public SuccessResponse<DepositorResponse> getReceiverNameByPhone(@RequestParam String phoneNumber) {
		return SuccessResponse.ok(walletService.getMemberByPhoneNumber(phoneNumber));
	}

	@ApiOperation(value = "지갑 송금", notes = "사용자의 지갑으로 금액을 송금합니다.")
	@PostMapping("/phone-num")
	public SuccessResponse<Void> transferPhone(
		@Valid @RequestBody TransferToWalletRequest request,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		walletService.transferToWallet(request, customUserDetails.getMember().getMemberId());
		return SuccessResponse.noContent();
	}

	@ApiOperation(value = "계좌번호로 예금주명 확인 API", notes = "은행코드와 계좌번호로 예금주명을 확인합니다.")
	@GetMapping("/account")
	public SuccessResponse<DepositorResponse> getReceiverNameByAccount(
		@Valid @ModelAttribute AccountDepositorRequest verificationRequest, HttpServletRequest request
	) throws JsonProcessingException {
		DepositorResponse response = codefApiClient.verifyAccountHolder(verificationRequest, request);
		return SuccessResponse.ok(response);
	}

	@ApiOperation(value = "계좌번호 송금", notes = "계좌번호로 금액을 송금합니다.")
	@PostMapping("/account")
	public SuccessResponse<Void> transferAccount(
		@Valid @RequestBody TransferToAccountRequest request,
		@AuthenticationPrincipal CustomUserDetails customUserDetails
	) {
		walletService.transferToAccount(request, customUserDetails.getMember().getMemberId());
		return SuccessResponse.noContent();
	}
}
