package org.scoula.domain.financialproduct.jeonseloan.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import org.scoula.global.entity.BaseEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JeonseLoan extends BaseEntity {

	private Long jeonseLoanId;                    
	private Long financialCompanyId;              
	private String productName;                         
	private String joinWay;

	private BigDecimal minRate;                 
	private BigDecimal maxRate;                    
	private BigDecimal avgRate;                    

	private Integer visaMinMonths;        
	private Integer maxPeriodMonths;             
	private Integer minPeriodMonths;               


	private Long minLoanAmount;                   
	private Long maxLoanAmount;                    

	private Boolean foreignerAvailable; 
	private Boolean isActive; 

	private String loanConditions; 
	private String rateInfo; 
}
