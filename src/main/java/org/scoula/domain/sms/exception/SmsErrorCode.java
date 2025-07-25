package org.scoula.domain.sms.exception;

import org.scoula.global.exception.errorCode.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SmsErrorCode implements ErrorCode {

	SMS_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "S_001", "문자 메시지 전송에 실패하였습니다."),
	SMS_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "S_002", "인증번호가 일치하지 않습니다."),
	SMS_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "S_003", "인증번호가 만료되었습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
