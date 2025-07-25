package org.scoula.domain.location.exception;

import org.scoula.global.exception.errorCode.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LocationErrorCode implements ErrorCode {

	JsonParse_API_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "C-001", "json 파싱에 실패했습니다."),
	;
	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}

