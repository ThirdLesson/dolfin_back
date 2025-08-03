package org.scoula.domain.remmitanceinformation.entity;

import java.math.BigDecimal;

import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자_단체 송금 정보
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RemittanceInformation extends BaseEntity {

	private Long remittanceInformationId;   // PK
	private String receiverBank;          // 수취인 은행 이름
	private String swiftCode;             // SWIFT 코드
	private String routerCode;            // 라우터 코드(미국)
	private String receiverAccount;       // 수취인 계좌
	private String receiverName;          // 수취인 이름
	private String receiverNationality;   // 수취인 국가
	private String receiverAddress;       // 수취인 주소
	private String purpose;               // 거래목적
	private BigDecimal amount;            // 환전을 원하는 금액(원화)
	private Integer transmitFailCount;    // 송금 실패 카운트 (2회 이상 시 그룹 탈퇴)
	private IntermediaryBankCommission intermediaryBankCommission; // 중계 수수료 부담 방식

	public void updateRouterCode(String routerCode) {
		this.routerCode = routerCode;
	}
}
