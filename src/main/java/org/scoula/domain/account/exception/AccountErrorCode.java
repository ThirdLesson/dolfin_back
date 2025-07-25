package org.scoula.domain.account.exception;

import org.scoula.global.exception.errorCode.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountErrorCode implements ErrorCode {
	EXIST_ACCOUNT_NUMBER(HttpStatus.BAD_REQUEST, "A-001", "이미 등록돼있는 계좌번호 입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

}
