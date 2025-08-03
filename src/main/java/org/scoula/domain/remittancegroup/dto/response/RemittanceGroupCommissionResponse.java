package org.scoula.domain.remittancegroup.dto.response;

import java.math.BigDecimal;

public record RemittanceGroupCommissionResponse(
	BigDecimal originalCommission,
	BigDecimal benefitCommission,
	BigDecimal benefitAmount
) {
}
