package org.scoula.domain.financialproduct.depositsaving.dto.response;

import java.util.List;

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
	List<String> productCategories
) {
}
