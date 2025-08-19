package org.scoula.domain.financialproduct.depositsaving.dto.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DepositProduct(
	String typeCode,
	String code,
	String name,
	String companyCode,
	String companyName,
	boolean isBrokerage,
	String cuName,
	String interestRate,
	String primeInterestRate,
	String cmaInterestRate,
	List<String> features,
	List<String> productCategories,
	String joinTarget,  
	String note         
) {
}
