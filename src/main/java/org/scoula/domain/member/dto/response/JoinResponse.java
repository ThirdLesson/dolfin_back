package org.scoula.domain.member.dto.response;

import java.time.LocalDate;

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

	@ApiModelProperty(value = "생년월일", example = "19900101")
	LocalDate birth,

	@ApiModelProperty(value = "잔여 체류일", example = "20251231")
	LocalDate remainTime
) {
	public static JoinResponse of(Member member) {
		String nationality = null;
		if (member.getNationality() == null) {
			nationality = member.getCountry();
		} else {
			nationality = member.getNationality().getDescription();
		}

		return JoinResponse.builder()
			.name(member.getName())
			.passportNumber(member.getPassportNumber())
			.birth(member.getBirth())
			.remainTime(member.getRemainTime())
			.nationality(nationality)
			.build();
	}
}
