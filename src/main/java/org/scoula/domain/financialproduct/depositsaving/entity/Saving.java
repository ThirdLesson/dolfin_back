package org.scoula.domain.financialproduct.depositsaving.entity;

import java.math.BigDecimal;
import java.util.List;

import org.scoula.domain.financialproduct.constants.SavingSpclConditionType;
import org.scoula.global.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class Saving extends BaseEntity {
	private Long savingId;       
	private Long financialCompanyId;    
	private String name;                
	private Integer saveMonth;          
	private BigDecimal interestRate;     
	private BigDecimal maxInterestRate;  
	private String productCode;         
	private String companyCode;        
}
