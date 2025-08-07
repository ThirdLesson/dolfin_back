package org.scoula.domain.ledger.batch.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.scoula.domain.ledger.entity.LedgerType;
import org.scoula.domain.transaction.entity.TransactionType;

import lombok.Data;

@Data
public class LedgerVoucherWithEntryDTO {
	private Long ledgerVoucherId;
	private String voucherNo;
	private LocalDateTime entryDate;
	private TransactionType type;

	private Long ledgerEntryId;
	private BigDecimal amount;
	private LedgerType ledgerType;
	private Long accountCodeId;
}
