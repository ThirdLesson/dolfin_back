package org.scoula.domain.member.entity;

import java.time.LocalDate;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

	private Long memberId;          // 회원 아이디 (PK)
	private String loginId;         // 로그인 아이디
	private String password;        // 비밀번호
	private String passportNumber;  // 여권번호
	private String nationality;     // 국적
	private LocalDate birth;             // 생년월일
	private String name;            // 성명
	private String phoneNumber;     // 전화번호
	private LocalDate remainTime;        // 잔여 체류기간
	private String currency;        // 설정 통화

}
