package org.scoula.domain.remmitanceinformation.entity;

import java.math.BigDecimal;

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
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RemittanceInformation extends BaseEntity {

	private Long remittanceInformationId;   
	private String receiverBank;         
	private String swiftCode;            
	private String routerCode;          
	private String receiverAccount;       
	private String receiverName;         
	private String receiverNationality;   
	private String receiverAddress;      
	private String purpose;              
	private BigDecimal amount;          
	private Integer transmitFailCount;    
	private IntermediaryBankCommission intermediaryBankCommission; 

	public void updateRouterCode(String routerCode) {
		this.routerCode = routerCode;
	}
}
