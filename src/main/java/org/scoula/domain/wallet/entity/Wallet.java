package org.scoula.domain.wallet.entity;

import java.math.BigDecimal;

import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 지갑
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet extends BaseEntity {
	private Long walletId;			// pk
	private BigDecimal balance;		// 지갑 잔액
	private Integer password;		// 지갑 비밀번호
	private Long memberId;			// 회원 ID (FK)
}
