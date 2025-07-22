package org.scoula.global.security.exception;

import org.scoula.global.exception.errorCode.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtErrorMessage implements ErrorCode {

	INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "J-001", "유효하지 않은 JWT 토큰 입니다."),
	NEED_ACCESS_TOKEN_REFRESH(HttpStatus.UNAUTHORIZED, "J-002", "토큰 재발급이 필요합니다."),
	CANNOT_USE_REFRESH_TOKEN(HttpStatus.valueOf(400), "J-003", "리프레쉬 토큰은 사용할 수 없습니다."),
	UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "J-004", "지원하지 않는 유형의 JWT 토큰입니다."),
	NOT_MATCH_REFRESH_TOKEN(HttpStatus.valueOf(400), "J-005", "로그인이 필요한 서비스입니다."),
	SIGN_OUT_USER(HttpStatus.valueOf(400), "J-006", "이미 로그아웃한 사용자 입니다."),
	CLAIMS_IS_EMPTY(HttpStatus.UNAUTHORIZED, "J-007", "Claims 내부에 값이 들어있지 않습니다."),
	IN_JWT_NOT_CONTAINS_USER_INFO(HttpStatus.UNAUTHORIZED, "J-008", "토큰에 유저 정보가 담겨있지 않습니다."),
	SIGN_IN_FAIL(HttpStatus.valueOf(401), "J-009", "아이디 또는 비밀번호가 일치하지 않습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
