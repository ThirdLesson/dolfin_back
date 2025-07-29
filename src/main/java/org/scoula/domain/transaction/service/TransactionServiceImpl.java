package org.scoula.domain.transaction.service;

import static org.scoula.domain.transaction.entity.TransactionStatus.*;
import static org.scoula.domain.transaction.entity.TransactionType.*;

import java.math.BigDecimal;
import java.util.List;

import org.scoula.domain.member.entity.Member;
import org.scoula.domain.member.mapper.MemberMapper;
import org.scoula.domain.transaction.entity.Transaction;
import org.scoula.domain.transaction.mapper.TransactionMapper;
import org.scoula.domain.wallet.dto.request.TransferToAccountRequest;
import org.scoula.domain.wallet.entity.Wallet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

	private final TransactionMapper transactionMapper;
	private final MemberMapper memberMapper;

	@Transactional
	public void saveWalletTransferTransaction(Wallet senderWallet, BigDecimal senderNewBalance, Wallet receiverWallet,
		BigDecimal receiverNewBalance, Long memberId, Long receiverId, String transactionGroupId, BigDecimal amount) {
		Transaction senderTransaction = Transaction.builder()
			.walletId(senderWallet.getWalletId())
			.memberId(memberId)
			.transactionGroupId(transactionGroupId)
			.amount(amount)
			.beforeBalance(senderWallet.getBalance())
			.afterBalance(senderNewBalance)
			.transactionType(WALLET_TRANSFER)
			.counterPartyMemberId(receiverId)
			.counterPartyWalletId(receiverWallet.getWalletId())
			.status(SUCCESS)
			.build();
		transactionMapper.insert(senderTransaction);

		Transaction receiverTransaction = Transaction.builder()
			.walletId(receiverWallet.getWalletId())
			.memberId(receiverId)
			.transactionGroupId(transactionGroupId)
			.amount(amount)
			.beforeBalance(receiverWallet.getBalance())
			.afterBalance(receiverNewBalance)
			.transactionType(WALLET_TRANSFER)
			.counterPartyMemberId(senderWallet.getMemberId())
			.counterPartyWalletId(senderWallet.getWalletId())
			.status(SUCCESS)
			.build();
		transactionMapper.insert(receiverTransaction);
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
	public List<Transaction> getRecentAccountReceivers(Long memberId) {
		return transactionMapper.findByMemberIdAndAccountTransfer(memberId);
	}

	@Override
	public List<Member> getRecentWalletReceivers(Long memberId) {
		List<Transaction> transactions = transactionMapper.findByMemberIdAndWalletTransfer(memberId);
		List<Long> memberIds = transactions.stream().map(Transaction::getCounterPartyMemberId).toList();
		return memberMapper.selectMembersInIds(memberIds);
	}

	@Transactional
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
}
