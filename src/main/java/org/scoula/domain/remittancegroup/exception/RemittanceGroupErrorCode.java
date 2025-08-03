package org.scoula.domain.remittancegroup.exception;

import org.scoula.global.exception.errorCode.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RemittanceGroupErrorCode implements ErrorCode {

	ALREADY_JOINED_GROUP(HttpStatus.BAD_REQUEST, "RG-001", "이미 다른 단체 송금 그룹에 가입되어있는 회원입니다."),

	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
