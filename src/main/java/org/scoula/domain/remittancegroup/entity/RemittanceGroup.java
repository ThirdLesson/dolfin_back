package org.scoula.domain.remittancegroup.entity;

import org.scoula.global.constants.Currency;
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
public class RemittanceGroup extends BaseEntity {

	private Long remittanceGroupId;        
	private BenefitStatus benefitStatus; 
	private Integer remittanceDate; 
	private Integer memberCount; 
	private Currency currency; 

}

