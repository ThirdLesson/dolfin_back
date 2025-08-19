package org.scoula.domain.financialproduct.personalcreditloan.excpetion;

import org.scoula.global.exception.errorCode.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PersonalLoanErrorCode implements ErrorCode {

	EXCHANGE_NOT_FOUND(HttpStatus.NOT_FOUND, "M-001", "존재하지 대출 단위 입니다."),
	LOAN_NOT_FOUND(HttpStatus.NOT_FOUND, "M-002", "존재하지 않는 대출 상품입니다.")
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
