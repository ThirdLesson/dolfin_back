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
	EXCHANGE_REQUIRED_PARAMETER_MISSING(HttpStatus.BAD_REQUEST, "M-002", "환율 필수 파라미터가 누락되었습니다."),
	EXCHANGE_GRAPH_NOT_FOUND(HttpStatus.NOT_FOUND, "M-003", "환율 그래프를 찾을 수 없습니다."),
	EXCHANGE_AMOUNT_SMALL_THAN_ZERO(HttpStatus.BAD_REQUEST, "M-004", "환율 금액은 0보다 작을 수 없습니다.");


	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
