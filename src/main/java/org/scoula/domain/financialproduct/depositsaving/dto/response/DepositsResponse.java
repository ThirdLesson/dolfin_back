package org.scoula.domain.financialproduct.depositsaving.dto.response;

import java.util.List;

import org.scoula.domain.financialproduct.constants.DepositSpclConditionType;
import org.scoula.domain.financialproduct.depositsaving.dto.DepositDTO;
import org.scoula.domain.financialproduct.depositsaving.dto.common.DepositProduct;
import org.scoula.domain.financialproduct.depositsaving.entity.Deposit;
import org.scoula.domain.financialproduct.depositsaving.entity.DepositSpclCondition;
import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;
import org.scoula.domain.wallet.dto.response.DepositorResponse;

import lombok.Builder;

@Builder
public record DepositsResponse(
	DepositDTO product, 
	FinancialCompanyResponseDTO company
) {
	public static DepositsResponse of(Deposit deposit,
		FinancialCompany company,
		List<DepositSpclCondition> spclConditions) {

		List<DepositSpclConditionType> conditionTypes = spclConditions.stream()
			.map(DepositSpclCondition::getSpclCondition)
			.toList();

		DepositDTO productDTO = DepositDTO.fromEntity(deposit, conditionTypes);
		FinancialCompanyResponseDTO companyDTO = FinancialCompanyResponseDTO.fromEntity(company);

		return DepositsResponse.builder()
			.product(productDTO)
			.company(companyDTO)
			.build();
	}
}
