package org.scoula.domain.controller.ledger.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerCode {
	private Long accountCodeId;
	private AccountType type;         // 자산/부채/자본 등
	private Long ledgerEntryId;       // 분개 ID (FK)

}
