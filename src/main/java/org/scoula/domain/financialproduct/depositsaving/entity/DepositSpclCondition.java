package org.scoula.domain.financialproduct.depositsaving.entity;

import org.scoula.domain.financialproduct.constants.DepositSpclConditionType;
import org.scoula.global.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DepositSpclCondition extends BaseEntity {
	private Long id;
	private Long depositId; 
	private DepositSpclConditionType spclCondition; 
	private String productCode;        
	private String companyCode;         
	private Integer saveMonth;
}
