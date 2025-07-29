package org.scoula.domain.ledger.entity;

import java.math.BigDecimal;

import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerEntry extends BaseEntity {

	private Long ledgerEntryId;
	private BigDecimal amount;                // 금액
	private LedgerType ledgerType; 		// DEBIT(차변) CREDIT(대변)
	private Long ledgerVoucherId;       // 전표 ID (FK)

	private Long accountCodeId;         // 회계 코드 ID (FK)

	@Builder
	public LedgerEntry(BigDecimal amount, LedgerType ledgerType, Long ledgerVoucherId, Long accountCodeId) {
		this.amount = amount;
		this.ledgerType = ledgerType;
		this.ledgerVoucherId = ledgerVoucherId;
		this.accountCodeId = accountCodeId;
	}
}

