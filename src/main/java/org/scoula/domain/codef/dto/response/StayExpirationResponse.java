package org.scoula.domain.codef.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "체류 만료 기간 조회 응답 데이터 객체")
public record StayExpirationResponse(
	@ApiModelProperty(notes = "진위 확인 (0: false, 1: true)", example = "1", required = true)
	String resAuthenticity,
	@ApiModelProperty(notes = "진위 확인 내용 (resAuthenticity = '0'일 때 제공)", example = "현재 체류중인 외국인이 아닙니다.", required = false)
	String resAuthenticityDesc,
	@ApiModelProperty(notes = "여권번호 (resAuthenticity = '1'일 때 제공)", example = "E12345678")
	String resPassportNo,
	@ApiModelProperty(notes = "국적 (resAuthenticity = '1'일 때 제공)", example = "2")
	String resNationality,
	@ApiModelProperty(notes = "생년월일 (YYYYMMDD, resAuthenticity = '1'일 때 제공)", example = "19900101")
	String commBirthDate,
	@ApiModelProperty(notes = "상태 (resAuthenticity = '1'일 때 제공)", example = "VALID")
	String resStatus,
	@ApiModelProperty(notes = "만료일 (YYYYMMDD, resAuthenticity = '1'일 때 제공)", example = "20251231")
	String resExpirationDate
) {
}

