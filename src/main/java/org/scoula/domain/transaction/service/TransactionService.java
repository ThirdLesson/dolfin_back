package org.scoula.domain.transaction.service;

import java.math.BigDecimal;

import org.scoula.domain.wallet.entity.Wallet;

public interface TransactionService {
	void saveTransferTransaction(Wallet senderWallet, BigDecimal senderNewBalance, Wallet receiverWallet,
		BigDecimal receiverNewBalance, Long memberId, Long receiverId, String transactionGroupId, BigDecimal amount);

	void saveChargeTransaction(Wallet senderWallet, BigDecimal senderNewBalance, Wallet receiverWallet,
		BigDecimal receiverNewBalance, Long memberId, Long receiverId, String transactionGroupId, BigDecimal amount);
}
