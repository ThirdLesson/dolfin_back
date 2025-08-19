package org.scoula.domain.wallet.entity;

import java.math.BigDecimal;

import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet extends BaseEntity {
	private Long walletId;           
	private BigDecimal balance;       
	private Integer password;        
	private Long memberId;           
}
