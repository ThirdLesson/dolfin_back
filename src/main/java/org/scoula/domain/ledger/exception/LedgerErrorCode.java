package org.scoula.domain.ledger.exception;

import org.scoula.global.exception.errorCode.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LedgerErrorCode implements ErrorCode {

	LEDGER_BALANCED_BROKEN(HttpStatus.INTERNAL_SERVER_ERROR, "L-001", "차변 대변 정합성 검증에 실패하였습니다."),

	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
