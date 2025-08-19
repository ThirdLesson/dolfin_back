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
	private BigDecimal amount;                
	private LedgerType ledgerType;        
	private Long ledgerVoucherId;       

	private Long accountCodeId;        

	@Builder
	public LedgerEntry(BigDecimal amount, LedgerType ledgerType, Long ledgerVoucherId, Long accountCodeId) {
		this.amount = amount;
		this.ledgerType = ledgerType;
		this.ledgerVoucherId = ledgerVoucherId;
		this.accountCodeId = accountCodeId;
	}
}

