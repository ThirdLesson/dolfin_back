package org.scoula.domain.codef.exception;

import org.scoula.global.exception.errorCode.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CodefErrorCode implements ErrorCode {
	CODEF_TOKEN_API_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C-001", "Codef 인증 API 연동에 실패했습니다."),
	CODEF_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "C-002", "Codef 토큰이 없습니다."),
	CODEF_STAY_EXPIRATION_API_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C-003", "체류기간 API 연동에 실패했습니다."),
	CODEF_REQUIRED_PARAMETER_MISSING(HttpStatus.BAD_REQUEST, "C-004", "필수 파라미터가 누락되었습니다."),
	STAY_AUTHENTICITY_FAILED(HttpStatus.BAD_REQUEST, "C-005", "현재 체류중이 아닙니다."),
	CODEF_VERIFICATION_CODE_FAIL(HttpStatus.BAD_REQUEST, "C-006", "계좌 1원 인증에 실패하였습니다.");
	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
