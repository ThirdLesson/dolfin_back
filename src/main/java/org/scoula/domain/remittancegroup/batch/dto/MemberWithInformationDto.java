package org.scoula.domain.remittancegroup.batch.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.scoula.domain.remmitanceinformation.entity.IntermediaryBankCommission;
import org.scoula.global.constants.Currency;
import org.scoula.global.constants.NationalityCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberWithInformationDto {
	private Long memberId;          
	private Long remittanceInformationId; 
	private Long remittanceGroupId; 
	private String passportNumber; 
	private NationalityCode nationality; 
	private String country;            
	private LocalDate birth;        
	private String name;            
	private String phoneNumber;     
	private LocalDate remainTime;   
	private Currency currency;      
	private String fcmToken; 

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
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
