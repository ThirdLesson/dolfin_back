package org.scoula.domain.ledger.entity;

import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회계 코드 테이블
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerCode extends BaseEntity {
	private Long accountCodeId;
	private String name;
	private AccountType type;         // ASSET(자산), LIABILITY(부채), EQUITY(자본), REVENUE(수익), EXPENSE(비용)

}
