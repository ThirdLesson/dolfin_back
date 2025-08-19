package org.scoula.domain.financialproduct.depositsaving.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import org.scoula.global.entity.BaseEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class Deposit extends BaseEntity {
	private Long depositId;       
	private Long financialCompanyId;    
	private String name;                
	private Integer saveMonth;            
	private BigDecimal interestRate;    
	private BigDecimal maxInterestRate;  
	private String productCode;        
	private String companyCode;        
}
