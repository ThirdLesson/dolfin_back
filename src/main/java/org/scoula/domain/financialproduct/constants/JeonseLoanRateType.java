package org.scoula.domain.financialproduct.constants;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ApiModel(description = "전세대출 금리 정렬 타입")
public enum JeonseLoanRateType {
	@ApiModelProperty("최소 금리")
	MIN_RATE("최저 금리"),

	@ApiModelProperty("최대 금리")
	MAX_RATE("최고 금리"),

	@ApiModelProperty("평균 금리")
	AVG_RATE("평균 금리");

	private final String name;
}
