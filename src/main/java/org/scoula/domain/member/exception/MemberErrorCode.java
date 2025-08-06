package org.scoula.domain.member.exception;

import org.scoula.global.exception.errorCode.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {

	// 400
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M-001", "존재하지 않는 회원입니다."),
	MEMBER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "M-002", "이미 존재하는 회원입니다."),
	LOGIN_ID_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "M-003", "이미 존재하는 회원 아이디 입니다."),
	STAY_EXPIRATION(HttpStatus.BAD_REQUEST, "M-004", "체류 기간이 만료되었습니다. 가입이 불가능합니다."),
	FCM_TOKEN_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "M-005", "fcm 토큰을 찾을 수 없습니다."),

	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
