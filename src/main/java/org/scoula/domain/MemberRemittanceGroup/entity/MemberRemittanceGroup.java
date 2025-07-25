package org.scoula.domain.MemberRemittanceGroup.entity;

import java.math.BigDecimal;

import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자_단체 송금 정보
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRemittanceGroup extends BaseEntity {

	private Long memberRemittanceGroup;   // PK
	private Long remittanceGroupId;       // 단체송금 아이디 (FK)
	private Long memberId;                // 회원 아이디 (FK)
	private String receiverBank;          // 수취인 은행 이름
	private String swiftCode;             // SWIFT 코드
	private String routerCode;            // 라우터 코드(미국)
	private String receiverAccount;       // 수취인 계좌
	private String receiverName;          // 수취인 이름
	private String receiverNationality;   // 수취인 국가
	private String receiverAddress;       // 수취인 주소
	private String purpose;               // 거래목적
	private BigDecimal amount;            // 환전을 원하는 금액(원화)
	private BigDecimal exchangeAmount;    // 환전이 완료된 금액
	private Status status;                  // 진행 / PENDING(진행중), SUCCESS(성공), FAILED(실패)
}
