package org.scoula.domain.exchange.exception;

import org.scoula.global.exception.errorCode.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExchangeErrorCode implements ErrorCode {

	// 400
	EXCHANGE_NOT_FOUND(HttpStatus.NOT_FOUND, "M-001", "존재하지 않는 화폐 입니다."),
	EXCHANGE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "M-002", "이미 존재하는 회원입니다."),
	EXCHANGE_REQUIRED_PARAMETER_MISSING(HttpStatus.BAD_REQUEST, "M-003", "환율 필수 파라미터가 누락되었습니다."),;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
