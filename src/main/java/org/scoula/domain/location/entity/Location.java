package org.scoula.domain.location.entity;

import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location extends BaseEntity {
	private Long locationId;        	 
	private String locationName;    	 
	private String address;         	  
	private Point point;    			 
	private String homepageUrl;    	  
	private String tel;  	  
	private LocationType locationType;   

}
