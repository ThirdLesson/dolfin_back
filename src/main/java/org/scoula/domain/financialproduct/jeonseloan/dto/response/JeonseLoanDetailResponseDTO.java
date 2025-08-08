package org.scoula.domain.financialproduct.jeonseloan.dto.response;

import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;
import org.scoula.domain.financialproduct.jeonseloan.dto.JeonseLoanDTO;
import org.scoula.domain.financialproduct.jeonseloan.entity.JeonseLoan;

public record JeonseLoanDetailResponseDTO(
	JeonseLoanDTO jeonseLoan,
	FinancialCompanyResponseDTO company
) {
	public static JeonseLoanDetailResponseDTO from(JeonseLoan entity, FinancialCompanyResponseDTO company,
		Boolean joinAvailable) {
		return new JeonseLoanDetailResponseDTO(
			JeonseLoanDTO.from(entity,joinAvailable),
			company
		);
	}
}
