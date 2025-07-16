package org.scoula.global.exception;

import org.scoula.global.exception.errorCode.ErrorCode;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
	private final ErrorCode errorCode;

	protected CustomException(ErrorCode errorCode, String message) {
		super(errorCode.getMessage() + ": " + message);
		this.errorCode = errorCode;
	}

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public CustomException(ErrorCode errorCode, Object... args) {
		super(String.format(errorCode.getMessage(), args));
		this.errorCode = errorCode;
	}
}
