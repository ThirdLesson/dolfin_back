package org.scoula.domain.ledger.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.scoula.domain.transaction.entity.TransactionType;
import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 전표 테이블
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerVoucher extends BaseEntity {
	private Long ledgerVoucherId;
	private String voucherNo;          // 전표 번호
	private Long transactionId;        // 거래내역 UUID
	private LocalDateTime entryDate;   // 거래 일자
	private TransactionType type;       // 거래 타입 (CHARGE(충전), TRANSFER(송금))

	private List<LedgerEntry> entries; // 분개 목록

}
