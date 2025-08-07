package org.scoula.domain.financialproduct.jeonseloan.dto.request;

import java.math.BigDecimal;

import org.scoula.domain.financialproduct.constants.JeonseLoanInterestFilterType;

public record JeonseLoanRequestDTO(
	JeonseLoanInterestFilterType jeonseLoanInterestFilterType
) {
}
