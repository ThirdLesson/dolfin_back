package org.scoula.global.exception.errorCode;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
	HttpStatus getHttpStatus();

	String getCode();

	String getMessage();
}
