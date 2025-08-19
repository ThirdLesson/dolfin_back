package org.scoula.domain.wallet.service;

import static org.scoula.domain.wallet.exception.WalletErrorCode.*;
import static org.scoula.global.constants.Currency.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.account.dto.request.CreateAccountRequest;
import org.scoula.domain.account.service.AccountService;
import org.scoula.domain.codef.dto.request.WalletRequest;
import org.scoula.domain.exchange.entity.ExchangeRate;
import org.scoula.domain.exchange.entity.Type;
import org.scoula.domain.exchange.mapper.ExchangeRateMapper;
import org.scoula.domain.ledger.service.LedgerService;
import org.scoula.domain.member.entity.Member;
import org.scoula.domain.member.mapper.MemberMapper;
import org.scoula.domain.member.service.MemberService;
import org.scoula.domain.remittancegroup.batch.dto.MemberWithInformationDto;
import org.scoula.domain.remittancegroup.entity.RemittanceGroup;
import org.scoula.domain.remittancegroup.mapper.RemittanceGroupMapper;
import org.scoula.domain.remmitanceinformation.mapper.RemittanceInformationMapper;
import org.scoula.domain.transaction.entity.Transaction;
import org.scoula.domain.transaction.service.TransactionService;
import org.scoula.domain.wallet.dto.request.ChargeWalletRequest;
import org.scoula.domain.wallet.dto.request.TransferToAccountRequest;
import org.scoula.domain.wallet.dto.request.TransferToWalletRequest;
import org.scoula.domain.wallet.dto.response.DepositorResponse;
import org.scoula.domain.wallet.dto.response.RecentAccountReceiversResponse;
import org.scoula.domain.wallet.dto.response.RecentWalletReceiversResponse;
import org.scoula.domain.wallet.dto.response.WalletResponse;
import org.scoula.domain.wallet.entity.Wallet;
import org.scoula.domain.wallet.mapper.WalletMapper;
import org.scoula.global.exception.CustomException;
import org.scoula.global.firebase.event.RemittanceFailedEvent;
import org.scoula.global.firebase.event.RemittanceGroupFiredEvent;
import org.scoula.global.firebase.event.RemittanceSuccessEvent;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
	private final RemittanceInformationMapper remittanceInformationMapper;
	private final RemittanceGroupMapper remittanceGroupMapper;
	private final MemberMapper memberMapper;
	private final ApplicationEventPublisher eventPublisher;
	private final ExchangeRateMapper exchangeRateMapper;

	private static final String BANK_NAME = "국민은행";
	private static final BigDecimal BENEFIT_PERCENTAGE = BigDecimal.valueOf(0.3);

	@Transactional
	@Override
	public void createWallet(WalletRequest walletRequest, Long memberId, HttpServletRequest request) {
		Wallet byMemberId = walletMapper.findByMemberId(memberId);

		if (byMemberId != null) {
			accountService.createAccount(new CreateAccountRequest(byMemberId.getWalletId(),
				memberId, walletRequest.getAccountNumber(), walletRequest.getBankType()), request);
			return;
		}

		walletMapper.createWallet(walletRequest, memberId);
		accountService.createAccount(new CreateAccountRequest(walletRequest.getWalletId(),
			memberId, walletRequest.getAccountNumber(), walletRequest.getBankType()), request);
	}

	@Override
	public WalletResponse getWalletByMember(Member member, HttpServletRequest request) {
		Wallet byMemberId = walletMapper.findByMemberId(member.getMemberId());
		if (byMemberId == null) {
			throw new CustomException(WALLET_NOT_FOUND, LogLevel.WARNING, null, Common.builder()
				.srcIp(request.getRemoteAddr())
				.callApiPath(request.getRequestURI())
				.apiMethod(request.getMethod())
				.deviceInfo(request.getHeader("user-Agent"))
				.build(),
				"조회 실패 ID: " + member.getMemberId());
		}

		return new WalletResponse(byMemberId.getWalletId(), byMemberId.getBalance());
	}

	@Transactional
	@Override
	public void transferToWallet(TransferToWalletRequest request, Member member, HttpServletRequest servletRequest) {

		Common common = Common.builder()
			.apiMethod(servletRequest.getMethod())
			.srcIp(servletRequest.getRemoteAddr())
			.callApiPath(servletRequest.getRequestURI())
			.deviceInfo(servletRequest.getHeader("host-Agent"))
			.build();

		Member receiver = memberService.getMemberByPhoneNumber(request.phoneNumber().replaceAll("-", ""),
			servletRequest);

		if (member.getMemberId().equals(receiver.getMemberId())) {
			throw new CustomException(SELF_TRANSFER_NOT_ALLOWED, LogLevel.WARNING, null, common,
				"송신자 ID: " + member.getMemberId() + ", 수신자 ID: " + receiver.getMemberId());
		}

		Long firstLockMemberId = (member.getMemberId().compareTo(receiver.getMemberId()) < 0) ? member.getMemberId() :
			receiver.getMemberId();
		Long secondLockMemberId =
			(member.getMemberId().compareTo(receiver.getMemberId()) < 0) ? receiver.getMemberId() :
				member.getMemberId();

		List<Long> ids = Arrays.asList(firstLockMemberId, secondLockMemberId)
			.stream().sorted().toList();
		List<Wallet> wallets = walletMapper.findByMemberIdsWithLock(ids);

		if (wallets.isEmpty()) {
			new CustomException(WALLET_NOT_FOUND, LogLevel.WARNING, null, common,
				"조회 실패 ID: " + firstLockMemberId + "or" + secondLockMemberId);
		}
		Wallet firstLockedWallet = wallets.get(0);
		Wallet secondLockedWallet = wallets.get(1);

		Wallet senderWallet =
			(firstLockedWallet.getMemberId().equals(member.getMemberId())) ? firstLockedWallet : secondLockedWallet;
		Wallet receiverWallet =
			(firstLockedWallet.getMemberId().equals(member.getMemberId())) ? secondLockedWallet : firstLockedWallet;

		if (senderWallet.getBalance().compareTo(request.amount()) < 0) {
			throw new CustomException(INSUFFICIENT_FUNDS, LogLevel.WARNING, null, common);
		}

		if (!request.password().equals(senderWallet.getPassword())) {
			throw new CustomException(INVALID_WALLET_PASSWORD, LogLevel.WARNING, null, common,
				"사용자 ID: " + member.getMemberId());
		}

		BigDecimal senderNewBalance = senderWallet.getBalance().subtract(request.amount());
		BigDecimal receiverNewBalance = receiverWallet.getBalance().add(request.amount());

		walletMapper.updateBothBalancesByMember(senderWallet.getMemberId(), senderNewBalance,
			receiverWallet.getMemberId(), receiverNewBalance);

		String transactionGroupId = UUID.randomUUID().toString();
		transactionService.saveWalletTransferTransaction(senderWallet, senderNewBalance, receiverWallet,
			receiverNewBalance,
			member, receiver, transactionGroupId, request.amount());

		ledgerService.accountForWalletTransfer(request, transactionGroupId, servletRequest);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void remittanceGroup(MemberWithInformationDto request, RemittanceGroup remittanceGroup) {
		Optional<Wallet> byMemberIdWithLock = walletMapper.findByMemberIdWithLock(request.getMemberId());
		if (byMemberIdWithLock.isEmpty()) {
			failRemittance(request, remittanceGroup);
			return;
		}
		Wallet lockedWallet = byMemberIdWithLock.get();

		if (lockedWallet.getBalance().compareTo(request.getAmount()) < 0) {
			failRemittance(request, remittanceGroup);
			return;
		}

		BigDecimal newBalance = lockedWallet.getBalance().subtract(request.getAmount());
		walletMapper.updateBalance(lockedWallet.getWalletId(), newBalance);

		String transactionGroupId = UUID.randomUUID().toString();
		transactionService.saveRemittanceTransaction(lockedWallet, newBalance, request, transactionGroupId);

		ledgerService.remittanceGroupTransfer(request, transactionGroupId);

		BigDecimal foreignAmount = calculateSendAmount(request);

		eventPublisher.publishEvent(new RemittanceSuccessEvent(request, foreignAmount, request.getCurrency()));
	}

	@Override
	public DepositorResponse getMemberByPhoneNumber(String phoneNumber, HttpServletRequest request) {
		phoneNumber = phoneNumber.replaceAll("-", "");
		return new DepositorResponse(memberService.getMemberByPhoneNumber(phoneNumber, request).getName());
	}

	@Transactional
	@Override
	public void transferToAccount(TransferToAccountRequest request, Long memberId, HttpServletRequest servletRequest) {

		Common common = Common.builder()
			.apiMethod(servletRequest.getMethod())
			.srcIp(servletRequest.getRemoteAddr())
			.callApiPath(servletRequest.getRequestURI())
			.deviceInfo(servletRequest.getHeader("host-Agent"))
			.build();

		Wallet senderWallet = walletMapper.findByMemberIdWithLock(memberId)
			.orElseThrow(
				() -> new CustomException(WALLET_NOT_FOUND, LogLevel.WARNING, null, common,
					"송신자 조회 실패 ID: " + memberId));

		if (senderWallet.getBalance().compareTo(request.amount()) < 0) {
			throw new CustomException(INSUFFICIENT_FUNDS, LogLevel.WARNING, null, common,
				"요청 금액: " + request.amount() + ", 현재 잔액: " + senderWallet.getBalance());
		}

		if (!request.password().equals(senderWallet.getPassword())) {
			throw new CustomException(INVALID_WALLET_PASSWORD, LogLevel.WARNING, null, common, "사용자 ID: " + memberId);
		}

		BigDecimal senderNewBalance = senderWallet.getBalance().subtract(request.amount());
		walletMapper.updateBalance(senderWallet.getWalletId(), senderNewBalance);

		accountService.depositToAccount(request, servletRequest);

		String transactionGroupId = UUID.randomUUID().toString();
		transactionService.saveAccountTransferTransaction(senderWallet, senderNewBalance, memberId, transactionGroupId,
			request);

		ledgerService.accountForAccountTransfer(request, transactionGroupId, servletRequest);
	}

	@Override
	public List<RecentAccountReceiversResponse> getRecentAccountReceivers(Member member) {
		List<Transaction> transactions = transactionService.getRecentAccountReceivers(member.getMemberId());
		return transactions.stream().map(transaction ->
			new RecentAccountReceiversResponse(transaction.getCounterPartyName(), transaction.getCounterPartyBankType(),
				transaction.getCounterPartyAccountNumber())).toList();
	}

	@Override
	public List<RecentWalletReceiversResponse> getRecentWalletReceivers(Member member) {
		List<Member> receivers = transactionService.getRecentWalletReceivers(member.getMemberId());
		return receivers.stream().map(receiver ->
			new RecentWalletReceiversResponse(receiver.getName(), formatKrPhone(receiver.getPhoneNumber()))).toList();
	}

	@Override
	public Wallet getWallet(Long walletId, HttpServletRequest request) {
		return validateWallet(walletId, request);
	}

	@Override
	@Transactional
	public void chargeWallet(ChargeWalletRequest chargeWalletRequest, Long walletId, HttpServletRequest request) {

		if (chargeWalletRequest.amount().compareTo(BigDecimal.valueOf(10000)) < 0) {
			throw new CustomException(NOT_ENOUGH_AMOUNT, LogLevel.WARNING, null,
				Common.builder().srcIp(request.getRemoteAddr()).callApiPath(request.getRequestURI()).apiMethod(
					request.getMethod()).deviceInfo(request.getHeader("user-agent")).build(),
				"최소 충전 금액부족, 요청 전자지갑 ID" + walletId);
		}


		Wallet wallet = Optional.ofNullable(walletMapper.findByWalletIdForUpdate(walletId)).orElseThrow(
			() -> new CustomException(NOT_EXIST_WALLET, LogLevel.WARNING, null,
				Common.builder().srcIp(request.getRemoteAddr()).callApiPath(request.getRequestURI()).apiMethod(
					request.getMethod()).deviceInfo(request.getHeader("user-agent")).build(),
				"전자지갑 조회에 실패하였습니다" + walletId)
		);

		if (!chargeWalletRequest.walletPassword().equals(wallet.getPassword())) {
			throw new CustomException(INVALID_WALLET_PASSWORD, LogLevel.WARNING, null,
				Common.builder()
					.deviceInfo(request.getHeader("user-agent"))
					.apiMethod(request.getMethod())
					.callApiPath(request.getRequestURI())
					.srcIp(request.getRemoteAddr())
					.build(), "전자지갑 ID: " + walletId + "전자지갑 비밀번호 불일치");
		}

		BigDecimal newBalance = wallet.getBalance().add(chargeWalletRequest.amount());

		walletMapper.updateBalance(walletId, newBalance);

		String transactionGroupId = UUID.randomUUID().toString();
		transactionService.saveChargeTransaction(
			wallet, newBalance,
			null, null,
			wallet.getMemberId(), null,
			transactionGroupId, chargeWalletRequest.amount());

		ledgerService.chargeTransfer(chargeWalletRequest, transactionGroupId, request);
	}

	@Transactional
	public void failRemittance(MemberWithInformationDto request, RemittanceGroup remittanceGroup) {
		int failCount = request.getTransmitFailCount() + 1;
		if (failCount >= 2) {
			remittanceInformationMapper.deleteById(request.getRemittanceInformationId());
			remittanceGroup.setMemberCount(remittanceGroup.getMemberCount() - 1);
			remittanceGroupMapper.updateMemberCountById(remittanceGroup.getRemittanceGroupId(),
				remittanceGroup.getMemberCount());
			memberMapper.updateRemittanceRefsStrict(request.getMemberId(), null, null);

			eventPublisher.publishEvent(new RemittanceGroupFiredEvent(request));
			return;
		}
		remittanceInformationMapper.incrementFailCount(request.getRemittanceInformationId());
		eventPublisher.publishEvent(new RemittanceFailedEvent(request));
	}

	private BigDecimal calculateSendAmount(MemberWithInformationDto request) {
		ExchangeRate sendRate = exchangeRateMapper.findLatestExchangeRate(BANK_NAME, Type.SEND.name(),
			request.getCurrency().name());

		ExchangeRate baseRate = exchangeRateMapper.findLatestExchangeRate(BANK_NAME, Type.BASE.name(),
			request.getCurrency().name());

		BigDecimal spread = sendRate.getExchangeValue().subtract(baseRate.getExchangeValue());
		BigDecimal multiply = spread.multiply(BigDecimal.ONE.subtract(BENEFIT_PERCENTAGE));
		BigDecimal lastRate = baseRate.getExchangeValue().add(multiply);

		if (request.getCurrency().equals(VND) || request.getCurrency().equals(JPY) || request.getCurrency()
			.equals(IDR)) {
			lastRate = lastRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
		}

		return request.getAmount().divide(lastRate, 2, RoundingMode.HALF_UP);
	}

	private Wallet validateWallet(Long walletId, HttpServletRequest request) {
		return Optional.ofNullable(walletMapper.findByWalletId(walletId)).orElseThrow(
			() -> new CustomException(NOT_EXIST_WALLET, LogLevel.WARNING, null,
				Common.builder().srcIp(request.getRemoteAddr()).callApiPath(request.getRequestURI()).apiMethod(
					request.getMethod()).deviceInfo(request.getHeader("user-agent")).build())
		);
	}

	public static String formatKrPhone(String raw) {
		if (raw == null || raw.isBlank())
			return raw;

		String s = raw.replaceAll("\\D", ""); 

		if (s.startsWith("02")) {
			if (s.length() == 9) {        
				return "02-" + s.substring(2, 5) + "-" + s.substring(5);
			} else if (s.length() == 10) { 
				return "02-" + s.substring(2, 6) + "-" + s.substring(6);
			} else {
				return raw; 
			}
		}

		if (s.length() == 10) {      
			return s.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
		} else if (s.length() == 11) { 
			return s.replaceFirst("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
		}

		return raw;
	}

}
