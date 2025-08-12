package org.scoula.domain.remittancegroup.exception;

import org.scoula.global.exception.errorCode.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RemittanceGroupErrorCode implements ErrorCode {

	ALREADY_JOINED_GROUP(HttpStatus.BAD_REQUEST, "RG-001", "이미 다른 단체 송금 그룹에 가입되어있는 회원입니다."),
	NOT_EXIST_GROUP(HttpStatus.NOT_FOUND, "RG-002", "존재하지 않는 단체 송금 그룹입니다."),
	CAN_NOT_GROUP_NUMBER_ZERO(HttpStatus.BAD_REQUEST, "RG-003", "단체 송금 그룹의 인원 수는 0명이 될 수 없습니다."),
	MEMBER_COUNT_NUMBER_ZERO(HttpStatus.BAD_REQUEST, "RG-004", "단체 송금 그룹의 인원 수는 0명이 될 수 없습니다."),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
