package org.scoula.domain.transaction.service;

import static org.scoula.domain.transaction.entity.TransactionStatus.*;
import static org.scoula.domain.transaction.entity.TransactionType.*;

import java.math.BigDecimal;

import org.scoula.domain.transaction.entity.Transaction;
import org.scoula.domain.transaction.mapper.TransactionMapper;
import org.scoula.domain.wallet.entity.Wallet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

	private final TransactionMapper transactionMapper;

	@Transactional
	public void saveTransferTransaction(Wallet senderWallet, BigDecimal senderNewBalance, Wallet receiverWallet,
		BigDecimal receiverNewBalance, Long memberId, Long receiverId, String transactionGroupId, BigDecimal amount) {
		Long receiverWalletId = null;
		if (receiverId != null) {
			receiverWalletId = receiverWallet.getWalletId();
		}
		Transaction senderTransaction = Transaction.builder()
			.walletId(senderWallet.getWalletId())
			.memberId(memberId)
			.transactionGroupId(transactionGroupId)
			.amount(amount)
			.beforeBalance(senderWallet.getBalance())
			.afterBalance(senderNewBalance)
			.transactionType(TRANSFER)
			.counterPartyMemberId(receiverId)
			.counterPartyWalletId(receiverWalletId)
			.status(SUCCESS)
			.build();
		transactionMapper.insert(senderTransaction);

		if (receiverId != null) {
			Transaction receiverTransaction = Transaction.builder()
				.walletId(receiverWallet.getWalletId())
				.memberId(receiverId)
				.transactionGroupId(transactionGroupId)
				.amount(amount)
				.beforeBalance(receiverWallet.getBalance())
				.afterBalance(receiverNewBalance)
				.transactionType(TRANSFER)
				.counterPartyMemberId(senderWallet.getMemberId())
				.counterPartyWalletId(senderWallet.getWalletId())
				.status(SUCCESS)
				.build();
			transactionMapper.insert(receiverTransaction);
		}

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
