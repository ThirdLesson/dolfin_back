package org.scoula.domain.financialproduct.depositsaving.entity;

import org.scoula.domain.financialproduct.constants.DepositSpclConditionType;
import org.scoula.domain.financialproduct.constants.SavingSpclConditionType;
import org.scoula.global.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class SavingSpclCondition extends BaseEntity {
	private Long id;
	private Long savingId; 
	private SavingSpclConditionType spclCondition; 
	private String productCode;        
	private String companyCode;         
	private Integer saveMonth;      
}
