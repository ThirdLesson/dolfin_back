package org.scoula.domain.member.errorCode;

import org.scoula.global.exception.errorCode.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberErrorCode implements ErrorCode {
	STAY_EXPIRATION(HttpStatus.BAD_REQUEST, "M-001", "체류 기간이 만료되었습니다. 가입이 불가능합니다.");
	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
