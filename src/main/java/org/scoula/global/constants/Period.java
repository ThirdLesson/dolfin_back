package org.scoula.global.constants;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Period {
	ONE_WEEK(7, ChronoUnit.DAYS),
	ONE_MONTH(1, ChronoUnit.MONTHS),
	THREE_MONTH(3, ChronoUnit.MONTHS),
	SIX_MONTH(6, ChronoUnit.MONTHS),
	ONE_YEAR(1, ChronoUnit.YEARS),
	TWO_YEAR(2, ChronoUnit.YEARS),
	;

	private final int value;
	private final ChronoUnit unit;

	public LocalDateTime getStartDate(LocalDateTime endDate) {
		return endDate.minus(value, unit);
	}
}
