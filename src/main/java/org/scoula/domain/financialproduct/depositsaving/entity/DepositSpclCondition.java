package org.scoula.domain.financialproduct.depositsaving.entity;

import org.scoula.domain.financialproduct.constants.DepositSpclConditionType;
import org.scoula.global.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// 우대금리
@Getter
@AllArgsConstructor
@Builder
public class DepositSpclCondition extends BaseEntity {
	private Long id;//PK
	private Long depositId; // FK
	private DepositSpclConditionType spclCondition; // ENUM, 우대조건
	private String productCode;         // 상품코드 (조인키)
	private String companyCode;         // 회사코드 (조인키)
	private Integer saveMonth;
}
