package org.scoula.domain.transaction.service;

import java.math.BigDecimal;
import java.util.List;

import org.scoula.domain.member.entity.Member;
import org.scoula.domain.transaction.dto.response.TransactionHistoryResponse;
import org.scoula.domain.transaction.entity.Transaction;
import org.scoula.domain.transaction.entity.TransactionType;
import org.scoula.domain.wallet.dto.request.TransferToAccountRequest;
import org.scoula.domain.wallet.entity.Wallet;
import org.scoula.global.constants.Period;
import org.scoula.global.constants.SortDirection;
import org.springframework.data.domain.Page;

public interface TransactionService {
	void saveWalletTransferTransaction(Wallet senderWallet, BigDecimal senderNewBalance, Wallet receiverWallet,
		BigDecimal receiverNewBalance, Long memberId, Long receiverId, String transactionGroupId, BigDecimal amount);

	void saveChargeTransaction(Wallet senderWallet, BigDecimal senderNewBalance, Wallet receiverWallet,
		BigDecimal receiverNewBalance, Long memberId, Long receiverId, String transactionGroupId, BigDecimal amount);

	void saveAccountTransferTransaction(Wallet senderWallet, BigDecimal senderNewBalance, Long memberId,
		String transactionGroupId, TransferToAccountRequest request);

	List<Transaction> getRecentAccountReceivers(Long memberId);

	List<Member> getRecentWalletReceivers(Long memberId);

	Page<TransactionHistoryResponse> getTransactionHistory(Period period, TransactionType type,
		BigDecimal minAmount, BigDecimal maxAmount, SortDirection sortDirection, int page, int size, Member member);
}
