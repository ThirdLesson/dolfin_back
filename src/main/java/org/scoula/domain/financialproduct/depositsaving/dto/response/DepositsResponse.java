package org.scoula.domain.financialproduct.depositsaving.dto.response;

import org.scoula.domain.financialproduct.depositsaving.dto.DepositSavingDTO;
import org.scoula.domain.financialproduct.depositsaving.entity.Deposit;
import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;

import lombok.Builder;

@Builder
public record DepositsResponse(
	DepositSavingDTO product, // 상품정보
	FinancialCompanyResponseDTO company// 회사정보
) {

	public static DepositsResponse fromEntity(Deposit deposit, FinancialCompany company) {

		// 상품 정보 DTO 생성
		DepositSavingDTO productDTO = DepositSavingDTO.builder()
			.depositSavingId(deposit.getDepositSavingId())
			.name(deposit.getName())
			.interestRate(deposit.getInterestRate())
			.maxInterestRate(deposit.getMaxInterestRate())
			.saveMonth(deposit.getSaveMonth())
			.spclCondition(deposit.getSpclCondition().toString())
			.financialCompanyId(deposit.getFinancialCompanyId())
			.build();

		// 회사 정보 DTO 생성
		FinancialCompanyResponseDTO companyDTO = FinancialCompanyResponseDTO.fromEntity(company);

		// DepositsResponse
		return DepositsResponse.builder()
			.product(productDTO)
			.company(companyDTO)
			.build();
	}
}
