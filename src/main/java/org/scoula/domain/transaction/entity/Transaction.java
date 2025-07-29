package org.scoula.domain.transaction.entity;

import java.math.BigDecimal;

import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 거래 내역
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends BaseEntity {
	private Long transactionId;					// pk
	private String transactionGroupId;            // 하나의 거래 상황을 그룹으로 묶어주는 아이디
	private BigDecimal amount;					// 거래 금액
	private BigDecimal beforeBalance;			// 충전 전 금액
	private BigDecimal afterBalance;			// 충전 후 금액
	private TransactionType transactionType;	// 거래 타입 (충전, 송금)
	private Long CounterPartyMemberId;			// 상대 회원 ID (거래 상대방이 있을 경우 기록)
	private Long CounterPartyWalletId;			// 상대 지갑 ID (거래 상대방이 있을 경우 기록)
	private TransactionStatus status;           // 거래 상태 (PENDING(대기중), SUCCESS(완료), FAILED(실패))
	private Long walletId;
	private Long memberId;

	@Builder
	public Transaction(String transactionGroupId, BigDecimal amount, BigDecimal beforeBalance, BigDecimal afterBalance,
		TransactionType transactionType, Long counterPartyMemberId, Long counterPartyWalletId,
		TransactionStatus status, Long walletId, Long memberId) {
		this.transactionGroupId = transactionGroupId;
		this.amount = amount;
		this.beforeBalance = beforeBalance;
		this.afterBalance = afterBalance;
		this.transactionType = transactionType;
		CounterPartyMemberId = counterPartyMemberId;
		CounterPartyWalletId = counterPartyWalletId;
		this.status = status;
		this.walletId = walletId;
		this.memberId = memberId;
	}
}
