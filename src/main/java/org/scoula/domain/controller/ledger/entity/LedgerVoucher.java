package org.scoula.domain.controller.ledger.entity;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerVoucher {
	private Long ledgerVoucherId;
	private String voucherNo;          // 전표 번호
	private Long transactionId;        // 거래내역
	private LocalDateTime entryDate;   // 거래 일자
	private VoucherType voucherType;   // 전표 타입

	private List<LedgerEntry> entries; // 분개 목록

}
