package org.scoula.domain.codef.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;

@ApiModel(description = "체류 만료 기간 조회 요청 객체")
@Builder
public record StayExpirationRequest(
	@ApiModelProperty(notes = "기관코드 (고정 값 '0001')", example = "0001", required = true)
	String organization,
	@ApiModelProperty(notes = "여권번호", example = "E12345678", required = true)
	String passportNo,
	@ApiModelProperty(notes = "국적 코드 (0:러시아, 1:몽골, ..., 99:기타)", example = "2", required = true)
	String nationality,
	@ApiModelProperty(notes = "국적 '99'일 때 국가명", example = "가나", required = false)
	String country,
	@ApiModelProperty(notes = "생년월일 (YYYYMMDD)", example = "19900101", required = true)
	String birthDate
) {
}


