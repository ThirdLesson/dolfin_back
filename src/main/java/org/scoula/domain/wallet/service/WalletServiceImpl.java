package org.scoula.domain.wallet.service;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.account.dto.request.CreateAccountRequest;
import org.scoula.domain.account.service.AccountService;
import org.scoula.domain.codef.dto.request.WalletRequest;
import org.scoula.domain.member.dto.MemberDTO;
import org.scoula.domain.member.exception.MemberErrorCode;
import org.scoula.domain.member.service.MemberService;
import org.scoula.domain.wallet.dto.response.WalletResponse;
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
public class WalletServiceImpl implements WalletService {

	private final WalletMapper walletMapper;
	private final AccountService accountService;
	private final MemberService memberService;

	@Override
	public void createWallet(WalletRequest walletRequest, Long memberId, HttpServletRequest request) {
		MemberDTO memberById = Optional.ofNullable(memberService.getMemberById(memberId))
			.orElseThrow(() -> new CustomException(
				MemberErrorCode.MEMBER_NOT_FOUND,
				LogLevel.WARNING,
				"회원 정보를 찾을 수 없습니다.",
				Common.builder().build()
			));

		Wallet byMemberId = walletMapper.findByMemberId(memberId);

		if (byMemberId != null) {
			System.out.println("여기 맞음??");
			System.out.println("byMemberId.getWalletId() = " + byMemberId.getWalletId());
			accountService.createAccount(new CreateAccountRequest(byMemberId.getWalletId(),
				memberId, walletRequest.getAccountNumber(), walletRequest.getBankType()), request);
			return;
		}

		walletMapper.createWallet(walletRequest, memberId);
		System.out.println("walletRequest = " + walletRequest.getWalletId());
		accountService.createAccount(new CreateAccountRequest(walletRequest.getWalletId(),
			memberId, walletRequest.getAccountNumber(), walletRequest.getBankType()), request);
	}

	@Override
	public WalletResponse getWalletByMemberId(Long MemberId) {
		Wallet byMemberId = walletMapper.findByMemberId(MemberId);
		if (byMemberId == null) {
			return new WalletResponse(null, null);
		}

		return new WalletResponse(byMemberId.getWalletId(), byMemberId.getBalance());
	}
}
