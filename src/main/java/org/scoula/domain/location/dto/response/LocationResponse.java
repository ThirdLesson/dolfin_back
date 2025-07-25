package org.scoula.domain.location.dto.response;

import org.scoula.domain.location.entity.LocationType;
import org.scoula.domain.location.entity.Point;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;

@Builder
@ApiModel(description = "위치 정보 응답 데이터 객체")
public record LocationResponse(

	@ApiModelProperty(notes = "위치 이름", example = "서울특별시 도봉구 도봉로 552(창동) 도봉구민회관 2층 도봉구 다문화가족지원센터")
	String locationName,          // 위치 이름
	@ApiModelProperty(notes = "위치 주소", example = "서울특별시 도봉구 도봉로 552(창동) 도봉구민회관 2층 도봉구 다문화가족지원센터")
	String address,          // 주소
	@ApiModelProperty(notes = "위치 위도, 경도 정보")
	Point point,          // 위도, 경도
	@ApiModelProperty(notes = "전화번호", example = "02-1234-5678")
	String locationNumber,      // 전화번호
	@ApiModelProperty(notes = "장소 타입 (CENTER: 외국인 센터, BANK: 은행)")
	LocationType locationType    // 장소 타입 CENTER(은행), CONSULT(외국인 센터), BANK(은행)
) {
}
