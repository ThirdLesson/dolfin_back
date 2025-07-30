package org.scoula.domain.transaction.service;

import java.math.BigDecimal;
import java.util.List;

import org.scoula.domain.member.entity.Member;
import org.scoula.domain.transaction.entity.Transaction;
import org.scoula.domain.wallet.dto.request.TransferToAccountRequest;
import org.scoula.domain.wallet.entity.Wallet;

public interface TransactionService {
	void saveWalletTransferTransaction(Wallet senderWallet, BigDecimal senderNewBalance, Wallet receiverWallet,
		BigDecimal receiverNewBalance, Long memberId, Long receiverId, String transactionGroupId, BigDecimal amount);

	void saveChargeTransaction(Wallet senderWallet, BigDecimal senderNewBalance, Wallet receiverWallet,
		BigDecimal receiverNewBalance, Long memberId, Long receiverId, String transactionGroupId, BigDecimal amount);

	void saveAccountTransferTransaction(Wallet senderWallet, BigDecimal senderNewBalance, Long memberId,
		String transactionGroupId, TransferToAccountRequest request);

	List<Transaction> getRecentAccountReceivers(Long memberId);

	List<Member> getRecentWalletReceivers(Long memberId);
}
