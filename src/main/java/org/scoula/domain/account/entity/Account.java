package org.scoula.domain.account.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 계좌
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends BaseEntity {
	private Long accountId;    		// pk
	private String accountNumber;   // 계좌 번호
	private String password;		// 계좌 비밀번호
	private BigDecimal balance;		// 계좌 잔액
	private String bankCode;		// 은행 코드
	private String bankName;		// 은행명
	private boolean isVerified;		// 검증 여부
	private LocalDateTime verifiedAt;// 검증 시각
	private Long walletId;
	private Long memberId;
}
