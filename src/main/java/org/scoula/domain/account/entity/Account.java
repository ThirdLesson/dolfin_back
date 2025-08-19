package org.scoula.domain.account.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {
	private Long accountId;           
	private String accountNumber;   
	private String password;        
	private BigDecimal balance;       
	private BankType bankType; 
	private boolean isVerified;       
	private LocalDateTime verifiedAt;
	private Long walletId;
	private Long memberId;
}
