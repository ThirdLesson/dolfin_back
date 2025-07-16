package org.scoula.domain.ledger.entity;

import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerEntry extends BaseEntity {

	private Long ledgerEntryId;
	private Long amount;                // 금액
	private LedgerType ledgerType; 		// 차변/대변
	private LedgerCode ledgerCode;      // 회계 코드
	private Long ledgerVoucherId;       // 전표 ID (FK)

}

