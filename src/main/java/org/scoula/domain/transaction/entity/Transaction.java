package org.scoula.domain.transaction.entity;

import java.math.BigDecimal;

import org.scoula.domain.account.entity.BankType;
import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends BaseEntity {
	private Long transactionId;					
	private String transactionGroupId;        
	private BigDecimal amount;					
	private BigDecimal beforeBalance;			
	private BigDecimal afterBalance;			
	private TransactionType transactionType;	
	private String counterPartyName;           
	private Long counterPartyMemberId;           
	private Long counterPartyWalletId;            
	private BankType counterPartyBankType;      
	private String counterPartyAccountNumber;   
	private TransactionStatus status;           
	private Long walletId;
	private Long memberId;

	@Builder
	public Transaction(String transactionGroupId, BigDecimal amount, BigDecimal beforeBalance, BigDecimal afterBalance,
		TransactionType transactionType, String counterPartyName, Long counterPartyMemberId, Long counterPartyWalletId,
		BankType counterPartyBankType, String counterPartyAccountNumber, TransactionStatus status, Long walletId,
		Long memberId) {
		this.transactionGroupId = transactionGroupId;
		this.amount = amount;
		this.beforeBalance = beforeBalance;
		this.afterBalance = afterBalance;
		this.transactionType = transactionType;
		this.counterPartyName = counterPartyName;
		this.counterPartyMemberId = counterPartyMemberId;
		this.counterPartyWalletId = counterPartyWalletId;
		this.counterPartyBankType = counterPartyBankType;
		this.counterPartyAccountNumber = counterPartyAccountNumber;
		this.status = status;
		this.walletId = walletId;
		this.memberId = memberId;
	}
}
