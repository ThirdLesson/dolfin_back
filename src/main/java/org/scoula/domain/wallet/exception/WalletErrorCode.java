package org.scoula.domain.wallet.exception;

import org.scoula.global.exception.errorCode.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WalletErrorCode implements ErrorCode {

	WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "W-001", "지갑을 찾을 수 없습니다."),
	INSUFFICIENT_FUNDS(HttpStatus.BAD_REQUEST, "W-002", "잔액이 부족합니다."),
	INVALID_WALLET_PASSWORD(HttpStatus.UNAUTHORIZED, "W-003", "지갑 비밀번호가 일치하지 않습니다."),
	SELF_TRANSFER_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "W-004", "자기 자신에게 송금할 수 없습니다."),
	LEDGER_CODE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "W-005", "필수 회계 코드를 찾을 수 없습니다."),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

}
