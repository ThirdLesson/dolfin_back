package org.scoula.domain.member.dto.response;

import java.time.format.DateTimeFormatter;

import org.scoula.domain.member.entity.Member;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;

@ApiModel("회원 가입 응답")
@Builder
public record JoinResponse(
	@ApiModelProperty(value = "이름", example = "루나키키")
	String name,

	@ApiModelProperty(value = "여권 번호", example = "M1234567")
	String passportNumber,

	@ApiModelProperty(value = "국적", example = "대한민국")
	String nationality,

	@ApiModelProperty(value = "생년월일", example = "1990-01-01")
	String birth,

	@ApiModelProperty(value = "잔여 체류일", example = "2025-12-31")
	String remainTime
) {
	public static JoinResponse of(Member member) {
		String nationality = null;
		if (member.getNationality() == null) {
			nationality = member.getCountry();
		} else {
			nationality = member.getNationality().getDescription();
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		return JoinResponse.builder()
			.name(member.getName())
			.passportNumber(member.getPassportNumber())
			.birth(member.getBirth().format(formatter))
			.remainTime(member.getRemainTime().format(formatter))
			.nationality(nationality)
			.build();
	}
}
