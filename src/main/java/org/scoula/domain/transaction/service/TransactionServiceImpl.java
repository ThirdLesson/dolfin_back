package org.scoula.domain.transaction.service;

import static org.scoula.domain.transaction.entity.TransactionStatus.*;
import static org.scoula.domain.transaction.entity.TransactionType.*;
import static org.scoula.global.constants.Period.*;
import static org.scoula.global.constants.SortDirection.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.scoula.domain.member.entity.Member;
import org.scoula.domain.member.mapper.MemberMapper;
import org.scoula.domain.remittancegroup.batch.dto.MemberWithInformationDto;
import org.scoula.domain.transaction.dto.response.TransactionHistoryResponse;
import org.scoula.domain.transaction.dto.response.TransactionResponse;
import org.scoula.domain.transaction.entity.Transaction;
import org.scoula.domain.transaction.entity.TransactionType;
import org.scoula.domain.transaction.mapper.TransactionMapper;
import org.scoula.domain.wallet.dto.request.TransferToAccountRequest;
import org.scoula.domain.wallet.entity.Wallet;
import org.scoula.global.constants.Period;
import org.scoula.global.constants.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

	private final TransactionMapper transactionMapper;
	private final MemberMapper memberMapper;

	@Override
	public void saveWalletTransferTransaction(Wallet senderWallet, BigDecimal senderNewBalance, Wallet receiverWallet,
		BigDecimal receiverNewBalance, Member member, Member receiver, String transactionGroupId, BigDecimal amount) {
		Transaction senderTransaction = Transaction.builder()
			.walletId(senderWallet.getWalletId())
			.memberId(member.getMemberId())
			.transactionGroupId(transactionGroupId)
			.amount(amount)
			.beforeBalance(senderWallet.getBalance())
			.afterBalance(senderNewBalance)
			.transactionType(WALLET_TRANSFER)
			.counterPartyMemberId(receiver.getMemberId())
			.counterPartyWalletId(receiverWallet.getWalletId())
			.counterPartyName(receiver.getName())
			.status(SUCCESS)
			.build();

		Transaction receiverTransaction = Transaction.builder()
			.walletId(receiverWallet.getWalletId())
			.memberId(receiver.getMemberId())
			.transactionGroupId(transactionGroupId)
			.amount(amount)
			.beforeBalance(receiverWallet.getBalance())
			.afterBalance(receiverNewBalance)
			.transactionType(DEPOSIT)
			.counterPartyMemberId(senderWallet.getMemberId())
			.counterPartyWalletId(senderWallet.getWalletId())
			.counterPartyName(member.getName())
			.status(SUCCESS)
			.build();

		transactionMapper.insertTransferPair(senderTransaction, receiverTransaction);
	}

	@Override
	public void saveAccountTransferTransaction(Wallet senderWallet, BigDecimal senderNewBalance, Long memberId,
		String transactionGroupId, TransferToAccountRequest request) {
		Transaction senderTransaction = Transaction.builder()
			.walletId(senderWallet.getWalletId())
			.memberId(memberId)
			.transactionGroupId(transactionGroupId)
			.amount(request.amount())
			.beforeBalance(senderWallet.getBalance())
			.afterBalance(senderNewBalance)
			.transactionType(ACCOUNT_TRANSFER)
			.counterPartyName(request.name())
			.counterPartyBankType(request.bankType())
			.counterPartyAccountNumber(request.accountNumber())
			.status(SUCCESS)
			.build();
		transactionMapper.insert(senderTransaction);
	}

	@Override
	public void saveRemittanceTransaction(Wallet wallet, BigDecimal newBalance,
		MemberWithInformationDto request, String transactionGroupId) {
		Transaction senderTransaction = Transaction.builder()
			.walletId(wallet.getWalletId())
			.memberId(request.getMemberId())
			.transactionGroupId(transactionGroupId)
			.amount(request.getAmount())
			.beforeBalance(wallet.getBalance())
			.afterBalance(newBalance)
			.transactionType(WITHDRAW)
			.counterPartyName(request.getReceiverName())
			.counterPartyAccountNumber(request.getReceiverAccount())
			.status(SUCCESS)
			.build();
		transactionMapper.insert(senderTransaction);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Transaction> getRecentAccountReceivers(Long memberId) {
		return transactionMapper.findByMemberIdAndAccountTransfer(memberId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Member> getRecentWalletReceivers(Long memberId) {
		List<Transaction> transactions = transactionMapper.findByMemberIdAndWalletTransfer(memberId);
		List<Long> memberIds = transactions.stream().map(Transaction::getCounterPartyMemberId).toList();
		return memberMapper.selectMembersInIds(memberIds);
	}

	public void saveChargeTransaction(Wallet senderWallet, BigDecimal senderNewBalance, Wallet receiverWallet,
		BigDecimal receiverNewBalance, Long memberId, Long receiverId, String transactionGroupId, BigDecimal amount) {
		Long receiverWalletId = null;
		if (receiverId != null) {
			receiverWalletId = receiverWallet.getWalletId();
		}
		Transaction chargeTransaction = Transaction.builder()
			.walletId(senderWallet.getWalletId())
			.memberId(memberId)
			.transactionGroupId(transactionGroupId)
			.amount(amount)
			.beforeBalance(senderWallet.getBalance())
			.afterBalance(senderNewBalance)
			.transactionType(CHARGE)
			.counterPartyMemberId(receiverId)
			.counterPartyWalletId(receiverWalletId)
			.status(SUCCESS)
			.build();
		transactionMapper.insert(chargeTransaction);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<TransactionHistoryResponse> getTransactionHistory(Period period, TransactionType type,
		BigDecimal minAmount, BigDecimal maxAmount, SortDirection sortDirection, int page, Integer size,
		Member member) {

		List<TransactionType> types = null;
		if (type != null) {
			types = new ArrayList<>();
			types.add(type);
		}
		if (sortDirection == null)
			sortDirection = LATEST;
		if (period == null)
			period = ONE_MONTH;
		if (size == null)
			size = 20;
		if (type == WITHDRAW) {
			types.add(ACCOUNT_TRANSFER);
			types.add(WALLET_TRANSFER);
		}

		LocalDateTime endDate = LocalDateTime.now();
		LocalDateTime startDate = period.getStartDate(endDate);

		long totalElements = transactionMapper.countTransactionHistory(
			member.getMemberId(),
			startDate,
			endDate,
			types,
			minAmount,
			maxAmount
		);

		Pageable pageable = PageRequest.of(page, size, Sort.unsorted());

		List<Transaction> transactions = transactionMapper.findTransactionHistory(
			member.getMemberId(),
			startDate,
			endDate,
			types,
			minAmount,
			maxAmount,
			sortDirection,
			size,
			(int)pageable.getOffset()
		);

		Map<LocalDate, List<TransactionResponse>> groupedByDate = transactions.stream()
			.collect(Collectors.groupingBy(
				transaction -> transaction.getCreatedAt().toLocalDate(),
				Collectors.mapping(this::convertToTransactionResponse, Collectors.toList())
			));

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		List<TransactionHistoryResponse> historyResponseList = groupedByDate.entrySet().stream()
			.sorted(getComparatorForDateGrouping(sortDirection))
			.map(entry -> TransactionHistoryResponse.builder()
				.date(entry.getKey().format(formatter))
				.transactions(entry.getValue())
				.build())
			.toList();

		return new PageImpl<>(historyResponseList, pageable, totalElements);
	}

	@Transactional(readOnly = true)
	public BigDecimal getSumByTransactionType(TransactionType type, List<Transaction> transactions) {
		return transactions.stream()
			.filter(t -> t.getTransactionType() == type && t.getAmount() != null)
			.map(Transaction::getAmount)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private Comparator<Map.Entry<LocalDate, List<TransactionResponse>>> getComparatorForDateGrouping(
		SortDirection sortDirection) {
		if (sortDirection == OLDEST) {
			return Map.Entry.comparingByKey();
		} else {
			return Map.Entry.<LocalDate, List<TransactionResponse>>comparingByKey().reversed();
		}
	}

	private TransactionResponse convertToTransactionResponse(Transaction transaction) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
		TransactionType transactionType = transaction.getTransactionType();
		if (transaction.getTransactionType().equals(WALLET_TRANSFER) || transaction.getTransactionType()
			.equals(ACCOUNT_TRANSFER)) {
			transactionType = WITHDRAW;
		}
		return TransactionResponse.builder()
			.transactionId(transaction.getTransactionId())
			.type(transactionType)
			.amount(transaction.getAmount())
			.createdAt(transaction.getCreatedAt().format(formatter))
			.status(transaction.getStatus())
			.counterPartyName(transaction.getCounterPartyName())
			.build();
	}
}
