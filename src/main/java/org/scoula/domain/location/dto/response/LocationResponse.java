package org.scoula.domain.location.dto.response;

import org.scoula.domain.location.entity.LocationType;
import org.scoula.domain.location.entity.Point;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;

@Builder
@ApiModel(description = "위치 정보 응답 데이터 객체")
public record LocationResponse(

	@ApiModelProperty(notes = "위치 이름", example = "종로구 가족센터")
	String locationName,          
	@ApiModelProperty(notes = "위치 주소", example = "서울특별시 종로구 종로53길 29, 토월 2층")
	String address,        
	@ApiModelProperty(notes = "위치 위도, 경도 정보")
	Point point,         
	@ApiModelProperty(notes = "전화번호", example = "02-1234-5678")
	String tel,     
	@ApiModelProperty(notes = "홈페이지 주소", example = "https://example.com")
	String homepageUrl,     
	@ApiModelProperty(notes = "장소 타입 (CENTER: 외국인 센터, BANK: 은행)")
	LocationType locationType   
) {
}
