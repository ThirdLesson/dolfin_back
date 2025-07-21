package org.scoula.domain.member.dto;

import java.time.LocalDate;

import org.scoula.domain.member.entity.Member;
import org.scoula.global.constants.Currency;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "회원 정보")
public class MemberDTO {

	@ApiModelProperty(value = "회원 ID", example = "1")
	private Long memberId;

	@ApiModelProperty(value = "로그인 ID", example = "user001", required = true)
	private String loginId;

	@ApiModelProperty(value = "비밀번호", required = true)
	private String password;

	@ApiModelProperty(value = "여권번호", example = "M12345678")
	private String passportNumber;

	@ApiModelProperty(value = "국적", example = "KOR")
	private String nationality;

	@ApiModelProperty(value = "생년월일")
	private LocalDate birth;

	@ApiModelProperty(value = "성명", example = "홍길동", required = true)
	private String name;

	@ApiModelProperty(value = "전화번호", example = "010-1234-5678")
	private String phoneNumber;

	@ApiModelProperty(value = "잔여 체류기간")
	private LocalDate remainTime;

	@ApiModelProperty(value = "설정 통화", example = "USD")
	private Currency currency;

	// Entity -> DTO 변환
	public static MemberDTO from(Member member) {
		return MemberDTO.builder()
			.memberId(member.getMemberId())
			.loginId(member.getLoginId())
			.password(member.getPassword())
			.passportNumber(member.getPassportNumber())
			.nationality(member.getNationality())
			.birth(member.getBirth())
			.name(member.getName())
			.phoneNumber(member.getPhoneNumber())
			.remainTime(member.getRemainTime())
			.currency(member.getCurrency())
			.build();
	}

	// DTO -> Entity 변환
	public Member toEntity() {
		return Member.builder()
			.memberId(this.memberId)
			.loginId(this.loginId)
			.password(this.password)
			.passportNumber(this.passportNumber)
			.nationality(this.nationality)
			.birth(this.birth)
			.name(this.name)
			.phoneNumber(this.phoneNumber)
			.remainTime(this.remainTime)
			.currency(this.currency)
			.build();
	}

	public void changePassword(String newPassword) {
		this.password = newPassword;
	}
}
