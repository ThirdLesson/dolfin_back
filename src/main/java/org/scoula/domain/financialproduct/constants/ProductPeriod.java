package org.scoula.domain.financialproduct.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductPeriod {
	SIX_MONTH(6), ONE_YEAR(12), TWO_YEAR(24), STAY_EXPIRATION(0);

	private final int month;
}
