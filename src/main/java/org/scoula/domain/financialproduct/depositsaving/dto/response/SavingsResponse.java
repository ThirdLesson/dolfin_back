package org.scoula.domain.financialproduct.depositsaving.dto.response;

import java.util.List;

import org.scoula.domain.financialproduct.constants.SavingSpclConditionType;
import org.scoula.domain.financialproduct.depositsaving.dto.SavingDTO;
import org.scoula.domain.financialproduct.depositsaving.entity.Saving;
import org.scoula.domain.financialproduct.depositsaving.entity.SavingSpclCondition;
import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;

import lombok.Builder;

@Builder
public record SavingsResponse(
	SavingDTO product, // 상품정보
	FinancialCompanyResponseDTO company // 회사정보
) {
	public static SavingsResponse of(Saving saving,
		FinancialCompany company,
		List<SavingSpclCondition> spclConditions) {

		// Entity → Enum 변환
		List<SavingSpclConditionType> conditionTypes = spclConditions.stream()
			.map(SavingSpclCondition::getSpclCondition)
			.toList();

		SavingDTO productDTO = SavingDTO.fromEntity(saving, conditionTypes);
		FinancialCompanyResponseDTO companyDTO = FinancialCompanyResponseDTO.fromEntity(company);

		return SavingsResponse.builder()
			.product(productDTO)
			.company(companyDTO)
			.build();
	}
}
