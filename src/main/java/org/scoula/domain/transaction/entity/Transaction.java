package org.scoula.domain.transaction.entity;

import java.math.BigDecimal;
import java.util.UUID;

import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends BaseEntity {
	private Long transactionId;
	private UUID transactionGroupId;
	private BigDecimal amount;
	private BigDecimal beforeBalance;
	private BigDecimal afterBalance;
	private TransactionType transactionType;
	private Long CounterPartyMemberId;
	private Long CounterPartyWalletId;
	private TransactionStatus transactionStatus;
	private Long walletId;
	private Long memberId;
}
