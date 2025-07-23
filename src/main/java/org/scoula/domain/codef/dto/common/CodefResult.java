package org.scoula.domain.codef.dto.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Codef API 공통 결과 응답 객체")
public record CodefResult(
	@ApiModelProperty(notes = "API 호출 결과 코드", example = "CF-00000", required = true)
	String code,
	@ApiModelProperty(notes = "API 호출 결과 메시지", example = "성공", required = true)
	String message,
	@ApiModelProperty(notes = "API 호출 추가 메시지 (선택 사항)", example = "passportNo", required = false)
	String extraMessage,
	@ApiModelProperty(notes = "API 호출 트랜잭션 아이디", example = "f41d087dd6314", required = true)
	String transactionId
) {
}
