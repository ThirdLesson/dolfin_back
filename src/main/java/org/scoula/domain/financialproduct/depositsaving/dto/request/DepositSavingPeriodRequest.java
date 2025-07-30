package org.scoula.domain.financialproduct.depositsaving.dto.request;

import org.scoula.domain.financialproduct.constants.ProductPeriod;

public record DepositSavingPeriodRequest(
	ProductPeriod productPeriod
) {
}
