package org.scoula.domain.account.service;

import static org.scoula.domain.account.exception.AccountErrorCode.*;
import static org.scoula.domain.wallet.exception.WalletErrorCode.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.account.dto.request.CreateAccountRequest;
import org.scoula.domain.account.dto.response.AccountListResponse;
import org.scoula.domain.account.entity.Account;
import org.scoula.domain.account.exception.AccountErrorCode;
import org.scoula.domain.account.mapper.AccountMapper;
import org.scoula.domain.member.dto.MemberDTO;
import org.scoula.domain.member.exception.MemberErrorCode;
import org.scoula.domain.member.service.MemberService;
import org.scoula.domain.wallet.dto.request.TransferToAccountRequest;
import org.scoula.domain.wallet.entity.Wallet;
import org.scoula.domain.wallet.mapper.WalletMapper;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

	private final AccountMapper accountMapper;
	private final MemberService memberService;
	private final WalletMapper walletMapper;

	@Override
	public void createAccount(CreateAccountRequest createAccountRequest, HttpServletRequest request) {
		MemberDTO memberById = Optional.ofNullable(memberService.getMemberById(createAccountRequest.getMemberId()))
			.orElseThrow(() -> new CustomException(
				MemberErrorCode.MEMBER_NOT_FOUND,
				LogLevel.WARNING,
				"회원 정보를 찾을 수 없습니다.",
				Common.builder().build()
			));
		Account byAccountNumber = accountMapper.findByAccountNumber(createAccountRequest.getAccountNumber());
		if (byAccountNumber != null) {
			throw new CustomException(AccountErrorCode.EXIST_ACCOUNT_NUMBER, LogLevel.WARNING, null,
				Common.builder().deviceInfo(request.getHeader("host-agent")).srcIp(request.getRemoteAddr())
					.callApiPath(request.getRequestURI()).apiMethod(request.getMethod()).memberId(
						String.valueOf(createAccountRequest.getMemberId())).build());
		}
		accountMapper.createAccount(createAccountRequest);
	}

	@Transactional
	public void depositToAccount(TransferToAccountRequest request, HttpServletRequest servletRequest) {
		boolean externalTransferSuccess = true;

		if (!externalTransferSuccess) {
			throw new CustomException(ACCOUNT_TRANSFER_FAILED, LogLevel.ERROR, null, Common.builder()
				.deviceInfo(servletRequest.getHeader("user-agent"))
				.callApiPath(servletRequest.getRequestURI())
				.srcIp(servletRequest.getRemoteAddr())
				.apiMethod(servletRequest.getMethod())
				.build(),
				"은행 계좌 이체 실패: " + request.accountNumber());
		}
	}

	@Override
	public List<AccountListResponse> getAllAccountsByWalletId(Long walletId, HttpServletRequest request) {
		Wallet wallet = validateWallet(walletId, request);// 전자지갑이 존재하는지 검증
		List<Account> byWalletId = accountMapper.findByWalletId(walletId);
		List<AccountListResponse> accountListResponses = new ArrayList<>();
		for (Account account : byWalletId) {
			accountListResponses.add(new AccountListResponse(account.getAccountNumber(), account.getBankType().name()));
		}
		return accountListResponses;
	}

	private Wallet validateWallet(Long walletId, HttpServletRequest request) {
		return Optional.ofNullable(walletMapper.findByWalletId(walletId)).orElseThrow(
			() -> new CustomException(NOT_EXIST_WALLET, LogLevel.WARNING, null,
				Common.builder().srcIp(request.getRemoteAddr()).callApiPath(request.getRequestURI()).apiMethod(
					request.getMethod()).deviceInfo(request.getHeader("user-agent")).build())
		);
	}
}
