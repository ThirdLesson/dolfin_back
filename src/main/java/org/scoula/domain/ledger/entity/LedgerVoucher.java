package org.scoula.domain.ledger.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.scoula.domain.transaction.entity.TransactionType;
import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerVoucher extends BaseEntity {
	private Long ledgerVoucherId;
	private String voucherNo;          
	private String transactionId;       
	private LocalDateTime entryDate;  
	private TransactionType type;      

	private List<LedgerEntry> entries; 

	@Builder
	public LedgerVoucher(String voucherNo, String transactionId, LocalDateTime entryDate, TransactionType type) {
		this.voucherNo = voucherNo;
		this.transactionId = transactionId;
		this.entryDate = entryDate;
		this.type = type;
	}
}
