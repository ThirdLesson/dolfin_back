package org.scoula.domain.location.entity;

import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 위치 (외국인 센터, 은행)
 */
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location extends BaseEntity {
	private Long locationId;        	  // 위치 ID
	private String locationName;    	  // 위치 이름
	private String address;         	  // 주소
	private Point point;    			  // 위도, 경도
	private String locationNumber;  	  // 전화번호
	private LocationType locationType;    // 장소 타입 CENTER(은행), CONSULT(외국인 센터), BANK(은행)

}
