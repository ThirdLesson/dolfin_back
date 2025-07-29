package org.scoula.domain.wallet.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "예금주명 확인 응답", description = "예금주명 확인 API의 응답 데이터를 담는 DTO")
public record DepositorResponse(
	@ApiModelProperty(value = "예금주명", example = "홍길동")
	String name
) {
}