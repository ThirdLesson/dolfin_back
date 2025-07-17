package org.scoula.domain.ledger.entity;

import java.math.BigDecimal;

import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerEntry extends BaseEntity {

	private Long ledgerEntryId;
	private BigDecimal amount;                // 금액
	private LedgerType ledgerType; 		// DEBIT(차변) CREDIT(대변)
	private Long ledgerVoucherId;       // 전표 ID (FK)

	private Long legderCodeId;         // 회계 코드 ID (FK)
	private LedgerCode ledgerCode;      // 회계 코드

}

