package org.scoula.domain.financialproduct.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JeonseLoanInterestFilterType {
	MIN_RATE("최저 금리"), MAX_RATE("최고 금리"), AVG_RATE("평균 금리");

	private final String label;
}
