package org.scoula.domain.remittancegroup.batch.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

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
	// Member 정보
	private Long memberId;          // 회원 아이디 (PK)
	private Long remittanceInformationId; // 정기송금 정보 아이디
	private Long remittanceGroupId; // 정기송금 그룹 아이디
	private String passportNumber;  // 여권번호
	private NationalityCode nationality; // 국적
	private String country;            // 나라
	private LocalDate birth;        // 생년월일
	private String name;            // 성명
	private String phoneNumber;     // 전화번호
	private LocalDate remainTime;   // 잔여 체류기간
	private Currency currency;      // 설정 통화
	private String fcmToken; // firebase 유저 토큰

	// 해외 송금 정보
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
}
