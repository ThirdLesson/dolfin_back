package org.scoula.domain.financialproduct.jeonseloan.dto.request;

import org.scoula.domain.financialproduct.constants.JeonseLoanRateType;

public record JeonseLoanRequestDTO(
	JeonseLoanRateType sortBy,
	Long loanMinAmount,
	Long loanMaxAmount
) {
}
