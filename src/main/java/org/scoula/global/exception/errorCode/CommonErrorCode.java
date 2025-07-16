package org.scoula.global.exception.errorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {

	// 400
	INVALID_VALUE(HttpStatus.BAD_REQUEST, "C-001", "입력값이 올바르지 않습니다."),
	INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "C-002", "잘못된 인자입니다."),
	MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "C-005", "필수 파라미터가 누락되었습니다.")
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
