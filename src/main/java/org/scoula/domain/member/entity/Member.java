package org.scoula.domain.member.entity;

import java.time.LocalDate;

import org.scoula.global.constants.Currency;
import org.scoula.global.constants.NationalityCode;
import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Member extends BaseEntity {

	private Long memberId;          
	private Long remittanceInformationId; 
	private Long remittanceGroupId; 
	private String loginId;         
	private String password;        
	private String passportNumber;  
	private NationalityCode nationality; 
	private String country;            
	private LocalDate birth;        
	private String name;            
	private String phoneNumber;     
	private LocalDate remainTime;  
	private Currency currency;     
	private String connectedId; 
	private String fcmToken; 

}
