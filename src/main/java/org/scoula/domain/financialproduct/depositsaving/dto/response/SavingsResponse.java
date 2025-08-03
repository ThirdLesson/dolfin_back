package org.scoula.domain.financialproduct.depositsaving.dto.response;

import org.scoula.domain.financialproduct.depositsaving.dto.DepositDTO;
import org.scoula.domain.financialproduct.depositsaving.entity.Saving;
import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;

import lombok.Builder;

@Builder
public record SavingsResponse(
	DepositDTO product, // 상품정보
	FinancialCompanyResponseDTO company// 회사정보
) {
	// public static SavingsResponse fromEntity(Saving saving, FinancialCompany company) {
	//
	// 	// 상품 정보 DTO 생성
	// 	DepositDTO productDTO = DepositDTO.fromSavingEntity(saving);
	//
	// 	// 회사 정보 DTO 생성
	// 	FinancialCompanyResponseDTO companyDTO = FinancialCompanyResponseDTO.fromEntity(company);
	//
	// 	// SavingsResponse
	// 	return SavingsResponse.builder()
	// 		.product(productDTO)
	// 		.company(companyDTO)
	// 		.build();
	// }

}
