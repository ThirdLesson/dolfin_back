package org.scoula.domain.ledger.entity;

import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerCode extends BaseEntity {
	private Long accountCodeId;
	private AccountType type;         // 자산/부채/자본 등
	private Long ledgerEntryId;       // 분개 ID (FK)

}
