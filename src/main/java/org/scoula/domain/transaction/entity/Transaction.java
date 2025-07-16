package org.scoula.domain.transaction.entity;

import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends BaseEntity {
	private Long transactionId;
	private Long transactionGroupId;
	private Long amount;
	private TransactionType transactionType;
	private Long CounterPartyMemberId;
	private Long CounterPartyWalletId;
	private TransactionStatus transactionStatus;
	private Long walletId;
}
