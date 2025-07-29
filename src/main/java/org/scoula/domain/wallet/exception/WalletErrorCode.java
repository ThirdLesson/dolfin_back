package org.scoula.domain.wallet.exception;

import org.scoula.global.exception.errorCode.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WalletErrorCode implements ErrorCode {
	NOT_EXIST_WALLET(HttpStatus.BAD_REQUEST, "W-001", "해당 ID의 wallet이 존재하지 않습니다."),
	WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "W-002", "지갑을 찾을 수 없습니다."),
	INSUFFICIENT_FUNDS(HttpStatus.BAD_REQUEST, "W-003", "잔액이 부족합니다."),
	INVALID_WALLET_PASSWORD(HttpStatus.UNAUTHORIZED, "W-004", "지갑 비밀번호가 일치하지 않습니다."),
	SELF_TRANSFER_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "W-005", "자기 자신에게 송금할 수 없습니다."),
	LEDGER_CODE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "W-006", "필수 회계 코드를 찾을 수 없습니다."),
	NOT_ENOUGH_AMOUNT(HttpStatus.BAD_REQUEST, "W-007", "최소 충전 금액은 10,000원부터 입니다."),

	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

}
