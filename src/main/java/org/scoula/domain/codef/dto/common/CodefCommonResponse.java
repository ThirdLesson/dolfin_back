package org.scoula.domain.codef.dto.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Codef API 공통 응답 구조")
public record CodefCommonResponse<T>(
	@ApiModelProperty(notes = "API 호출 결과 상세", required = true)
	CodefResult result,
	@ApiModelProperty(notes = "API 상품별 데이터 페이로드")
	T data
) {
}
