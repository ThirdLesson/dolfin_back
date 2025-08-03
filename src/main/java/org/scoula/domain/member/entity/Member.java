package org.scoula.domain.member.entity;

import java.time.LocalDate;

import org.scoula.global.constants.Currency;
import org.scoula.global.constants.NationalityCode;
import org.scoula.global.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Member extends BaseEntity {

	private Long memberId;          // 회원 아이디 (PK)
	private Long remittanceInformationId; // 정기송금 정보 아이디
	private Long remittanceGroupId; // 정기송금 그룹 아이디
	private String loginId;         // 로그인 아이디
	private String password;        // 비밀번호
	private String passportNumber;  // 여권번호
	private NationalityCode nationality; // 국적
	private String country;            // 나라
	private LocalDate birth;        // 생년월일
	private String name;            // 성명
	private String phoneNumber;     // 전화번호
	private LocalDate remainTime;   // 잔여 체류기간
	private Currency currency;      // 설정 통화
	private String connectedId; // 코데프 유저 아이디
	private String fcmToken; // firebase 유저 토큰

}
