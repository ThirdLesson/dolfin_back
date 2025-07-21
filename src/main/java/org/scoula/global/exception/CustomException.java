package org.scoula.global.exception;

import org.scoula.global.exception.errorCode.ErrorCode;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
	private final ErrorCode errorCode;
	private final LogLevel logLevel;
	private final String txId;
	private final Common common;

	protected CustomException(ErrorCode errorCode, String message, LogLevel logLevel, String txId, Common common) {
		super(errorCode.getMessage() + ": " + message);
		this.errorCode = errorCode;
		this.logLevel = logLevel;
		this.txId = txId;
		this.common = common;
	}

	public CustomException(ErrorCode errorCode, LogLevel logLevel, String txId, Common common) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.logLevel = logLevel;
		this.txId = txId;
		this.common = common;
	}

	public CustomException(ErrorCode errorCode, LogLevel logLevel, String txId, Common common, Object... args) {
		super(String.format(errorCode.getMessage(), args));
		this.errorCode = errorCode;
		this.logLevel = logLevel;
		this.txId = txId;
		this.common = common;
	}
}
