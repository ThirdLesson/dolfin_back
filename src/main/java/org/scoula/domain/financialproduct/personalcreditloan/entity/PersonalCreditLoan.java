package org.scoula.domain.financialproduct.personalcreditloan.entity;

import java.math.BigDecimal;

import org.scoula.global.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalCreditLoan extends BaseEntity {

	private Long personalLoanId;          
	private Long financialCompanyId;     
	private String productName;          
	private BigDecimal baseRate;         
	private BigDecimal maxRate;          
	private BigDecimal spreadRate;       
	private BigDecimal maxLoanAmount;          
	private Integer minPeriodMonths;     
	private Integer maxPeriodMonths;      
	private String rateInfo;              
	private String loanConditions;       
	private Boolean foreignerAvailable;   
	private Integer visaMinMonths;        
	private Boolean isActive;            

	private String companyName;           
	private String companyCode;          

	private String companyCallNumber; 
	private String companyHomeUrl;    
}
