package org.scoula.domain.financialproduct.jeonseloan.dto.request;

import org.scoula.domain.financialproduct.constants.JeonseLoanRateType;

public record JeonseLoanRequestDTO(
	JeonseLoanRateType sortBy,
	Long minAmount,             // 최소 대출한도
	Long maxAmount // 최대 대출한도
) {
}
