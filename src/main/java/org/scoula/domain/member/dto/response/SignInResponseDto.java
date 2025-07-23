package org.scoula.domain.member.dto.response;

import java.time.LocalDate;

import org.scoula.domain.member.dto.MemberDTO;
import org.scoula.global.constants.Currency;
import org.scoula.global.constants.NationalityCode;
import org.scoula.global.security.dto.JwtToken;

import io.swagger.annotations.ApiModelProperty;

public record SignInResponseDto(
	String grantType,
	String accessToken,
	@ApiModelProperty(value = "회원 ID", example = "1")
	Long memberId,
	@ApiModelProperty(value = "여권번호", example = "M12345678")
	String passportNumber,
	@ApiModelProperty(value = "국적", example = "KOR")
	NationalityCode nationality,
	@ApiModelProperty(value = "생년월일")
	LocalDate birth,
	@ApiModelProperty(value = "성명", example = "홍길동", required = true)
	String name,
	@ApiModelProperty(value = "전화번호", example = "010-1234-5678")
	String phoneNumber,
	@ApiModelProperty(value = "잔여 체류기간")
	LocalDate remainTime,
	@ApiModelProperty(value = "설정 통화", example = "USD")
	Currency currency) {
	public static SignInResponseDto from(JwtToken token, MemberDTO member) {

		return new SignInResponseDto(token.getGrantType(),
			token.getAccessToken(),
			member.getMemberId(), member.getPassportNumber(), member.getNationality(), member.getBirth(),
			member.getName(), member.getPhoneNumber(), member.getRemainTime(), member.getCurrency());
	}
}
