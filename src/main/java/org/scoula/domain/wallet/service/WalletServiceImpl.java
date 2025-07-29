package org.scoula.domain.wallet.service;

import static org.scoula.domain.wallet.exception.WalletErrorCode.*;

import java.math.BigDecimal;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.account.dto.request.CreateAccountRequest;
import org.scoula.domain.account.service.AccountService;
import org.scoula.domain.codef.dto.request.WalletRequest;
import org.scoula.domain.ledger.service.LedgerService;
import org.scoula.domain.member.entity.Member;
import org.scoula.domain.member.service.MemberService;
import org.scoula.domain.transaction.service.TransactionService;
import org.scoula.domain.wallet.dto.request.TransferToAccountRequest;
import org.scoula.domain.wallet.dto.request.TransferToWalletRequest;
import org.scoula.domain.wallet.dto.response.DepositorResponse;
import org.scoula.domain.wallet.dto.response.WalletResponse;
import org.scoula.domain.wallet.entity.Wallet;
import org.scoula.domain.wallet.mapper.WalletMapper;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {

	private final WalletMapper walletMapper;
	private final AccountService accountService;
	private final MemberService memberService;
	private final LedgerService ledgerService;
	private final TransactionService transactionService;

	@Transactional
	@Override
	public void createWallet(WalletRequest walletRequest, Long memberId, HttpServletRequest request) {
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
	public WalletResponse getWalletByMember(Member member) {
		Wallet byMemberId = walletMapper.findByMemberId(member.getMemberId());
		if (byMemberId == null) {
			throw new CustomException(WALLET_NOT_FOUND, LogLevel.WARNING, null, null,
				"조회 실패 ID: " + member.getMemberId());
		}

		return new WalletResponse(byMemberId.getWalletId(), byMemberId.getBalance());
	}

	@Transactional
	@Override
	public void transferToWallet(TransferToWalletRequest request, Long memberId) {

		Member receiver = memberService.getMemberByPhoneNumber(request.phoneNumber());

		if (memberId.equals(receiver.getMemberId())) {
			throw new CustomException(SELF_TRANSFER_NOT_ALLOWED, LogLevel.WARNING, null, null,
				"송신자 ID: " + memberId + ", 수신자 ID: " + receiver.getMemberId());
		}

		Long firstLockMemberId = (memberId.compareTo(receiver.getMemberId()) < 0) ? memberId : receiver.getMemberId();
		Long secondLockMemberId = (memberId.compareTo(receiver.getMemberId()) < 0) ? receiver.getMemberId() : memberId;

		Wallet firstLockedWallet = walletMapper.findByMemberIdWithLock(firstLockMemberId)
			.orElseThrow(() -> new CustomException(WALLET_NOT_FOUND, LogLevel.WARNING, null, null,
				"조회 실패 ID: " + firstLockMemberId));
		Wallet secondLockedWallet = walletMapper.findByMemberIdWithLock(secondLockMemberId)
			.orElseThrow(() -> new CustomException(WALLET_NOT_FOUND, LogLevel.WARNING, null, null,
				"조회 실패 ID: " + secondLockMemberId));

		Wallet senderWallet =
			(firstLockedWallet.getMemberId().equals(memberId)) ? firstLockedWallet : secondLockedWallet;
		Wallet receiverWallet =
			(firstLockedWallet.getMemberId().equals(memberId)) ? secondLockedWallet : firstLockedWallet;

		if (senderWallet.getBalance().compareTo(request.amount()) < 0) {
			throw new CustomException(INSUFFICIENT_FUNDS, LogLevel.WARNING, null, null);
		}

		if (!request.password().equals(senderWallet.getPassword())) {
			throw new CustomException(INVALID_WALLET_PASSWORD, LogLevel.WARNING, null, null, "사용자 ID: " + memberId);
		}

		BigDecimal senderNewBalance = senderWallet.getBalance().subtract(request.amount());
		BigDecimal receiverNewBalance = receiverWallet.getBalance().add(request.amount());

		walletMapper.updateBalance(senderWallet.getWalletId(), senderNewBalance);
		walletMapper.updateBalance(receiverWallet.getWalletId(), receiverNewBalance);

		String transactionGroupId = UUID.randomUUID().toString();
		transactionService.saveTransferTransaction(senderWallet, senderNewBalance, receiverWallet, receiverNewBalance,
			memberId,
			receiver.getMemberId(), transactionGroupId, request.amount());

		ledgerService.accountForWalletTransfer(request, transactionGroupId);
	}

	@Override
	public DepositorResponse getMemberByPhoneNumber(String phoneNumber) {
		return new DepositorResponse(memberService.getMemberByPhoneNumber(phoneNumber).getName());
	}

	@Transactional
	@Override
	public void transferToAccount(TransferToAccountRequest request, Long memberId) {
		Wallet senderWallet = walletMapper.findByMemberIdWithLock(memberId)
			.orElseThrow(
				() -> new CustomException(WALLET_NOT_FOUND, LogLevel.WARNING, null, null, "송신자 조회 실패 ID: " + memberId));

		if (senderWallet.getBalance().compareTo(request.amount()) < 0) {
			throw new CustomException(INSUFFICIENT_FUNDS, LogLevel.WARNING, null, null,
				"요청 금액: " + request.amount() + ", 현재 잔액: " + senderWallet.getBalance());
		}

		if (!request.password().equals(senderWallet.getPassword())) {
			throw new CustomException(INVALID_WALLET_PASSWORD, LogLevel.WARNING, null, null, "사용자 ID: " + memberId);
		}

		BigDecimal senderNewBalance = senderWallet.getBalance().subtract(request.amount());
		walletMapper.updateBalance(senderWallet.getWalletId(), senderNewBalance);

		accountService.depositToAccount(request);

		String transactionGroupId = UUID.randomUUID().toString();
		transactionService.saveTransferTransaction(
			senderWallet, senderNewBalance,
			null, null,
			memberId, null,
			transactionGroupId, request.amount());

		ledgerService.accountForAccountTransfer(request, transactionGroupId);
	}
}
