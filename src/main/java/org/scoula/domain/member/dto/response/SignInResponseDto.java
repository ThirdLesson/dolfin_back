package org.scoula.domain.member.dto.response;

import java.time.format.DateTimeFormatter;

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
	@ApiModelProperty(value = "회원 로그인ID", example = "loginId")
	String loginId,
	@ApiModelProperty(value = "여권번호", example = "M12345678")
	String passportNumber,
	@ApiModelProperty(value = "국적", example = "KOR")
	NationalityCode nationality,
	@ApiModelProperty(value = "생년월일")
	String birth,
	@ApiModelProperty(value = "성명", example = "홍길동", required = true)
	String name,
	@ApiModelProperty(value = "전화번호", example = "01012345678")
	String phoneNumber,
	@ApiModelProperty(value = "잔여 체류기간")
	String remainTime,
	@ApiModelProperty(value = "설정 통화", example = "USD")
	Currency currency) {
	public static SignInResponseDto from(JwtToken token, MemberDTO member) {

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String birthStr = member.getBirth().format(dateFormatter);
		String remainStr = member.getRemainTime().format(dateFormatter);
		String phoneStr = formatPhoneNumber(member.getPhoneNumber());

		return new SignInResponseDto(token.getGrantType(),
			token.getAccessToken(),
			member.getMemberId(),
			member.getLoginId(),
			member.getPassportNumber(),
			member.getNationality(),
			birthStr,
			member.getName(),
			phoneStr,
			remainStr,
			member.getCurrency());
	}

	private static String formatPhoneNumber(String raw) {
		if (raw == null || raw.length() < 10)
			return raw;
		if (raw.length() == 10) {
			return raw.substring(0, 3) + "-" + raw.substring(3, 6) + "-" + raw.substring(6);
		}
		if (raw.length() == 11) {
			return raw.substring(0, 3) + "-" + raw.substring(3, 7) + "-" + raw.substring(7);
		}
		return raw;
	}
}
