package org.scoula.domain.financialproduct.exception;

import org.scoula.global.exception.errorCode.ErrorCode;
import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum FinancialErrorCode implements ErrorCode {

	FINANCIAL_COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "FC-001", "해당 금융회사를 찾을 수 없습니다"),
	FINANCIAL_COMPANY_ALREADY_EXISTS(HttpStatus.CONFLICT, "FC-002", "이미 존재하는 금융회사입니다"),
	INVALID_FINANCIAL_COMPANY_DATA(HttpStatus.BAD_REQUEST, "FC-003", "유효하지 않은 금융회사 데이터입니다"),
	FINANCIAL_COMPANY_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FC-004", "금융회사 저장에 실패했습니다"),
	FINANCIAL_COMPANY_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FC-005", "금융회사 수정에 실패했습니다"),
	FINANCIAL_COMPANY_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FC-006", "금융회사 삭제에 실패했습니다"),

	JEONSE_LOAN_NOT_FOUND(HttpStatus.NOT_FOUND, "JL-001", "해당 전세대출 상품을 찾을 수 없습니다"),
	JEONSE_LOAN_ALREADY_EXISTS(HttpStatus.CONFLICT, "JL-002", "이미 존재하는 전세대출 상품입니다"),
	JEONSE_LOAN_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JL-003", "전세대출 상품 저장에 실패했습니다"),
	JEONSE_LOAN_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JL-004", "전세대출 상품 수정에 실패했습니다"),
	JEONSE_LOAN_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JL-005", "전세대출 상품 삭제에 실패했습니다"),
	JEONSE_LOAN_BATCH_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JL-006", "전세대출 상품 배치 저장에 실패했습니다"),
	EMPTY_JEONSE_LOAN_LIST(HttpStatus.BAD_REQUEST, "JL-007", "전세대출 상품 목록이 비어있습니다"),

	JEONSE_LOAN_API_CALL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JL-API-001", "전세대출 API 호출에 실패했습니다"),
	JEONSE_LOAN_API_RESPONSE_EMPTY(HttpStatus.BAD_GATEWAY, "JL-API-002", "전세대출 API 응답이 비어있습니다"),
	JEONSE_LOAN_API_PARSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "JL-API-003", "전세대출 API 응답 파싱에 실패했습니다"),

	PERSONAL_LOAN_NOT_FOUND(HttpStatus.NOT_FOUND, "PCL-001", "해당 개인신용대출 상품을 찾을 수 없습니다"),
	PERSONAL_LOAN_ALREADY_EXISTS(HttpStatus.CONFLICT, "PCL-002", "이미 존재하는 개인신용대출 상품입니다"),
	DUPLICATE_PersonalCredit_Loan(HttpStatus.CONFLICT, "PCL-003", "중복된 개인신용대출 상품입니다"),
	PERSONAL_LOAN_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PCL-004", "개인신용대출 상품 저장에 실패했습니다"),
	PERSONAL_LOAN_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PCL-005", "개인신용대출 상품 수정에 실패했습니다"),
	PERSONAL_LOAN_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PCL-006", "개인신용대출 상품 삭제에 실패했습니다"),
	PERSONAL_LOAN_BATCH_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PCL-007", "개인신용대출 상품 배치 저장에 실패했습니다"),
	PERSONAL_LOAN_SYNC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PCL-008", "개인신용대출 상품 동기화에 실패했습니다"),

	DEPOSIT_SAVING_NOT_FOUND(HttpStatus.NOT_FOUND, "DS-001", "해당 예적금 상품을 찾을 수 없습니다"),
	DEPOSIT_SAVING_API_CALL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DS-API-001", "예적금 API 호출에 실패했습니다"),
	DEPOSIT_SAVING_BATCH_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DS-002", "예적금 상품 배치 저장에 실패했습니다"),

	EXTERNAL_API_CALL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "API-001", "외부 API 호출에 실패했습니다"),
	API_RESPONSE_EMPTY(HttpStatus.BAD_GATEWAY, "API-002", "API 응답이 비어있습니다"),
	API_RESPONSE_PARSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "API-003", "API 응답 파싱에 실패했습니다"),
	API_RESPONSE_INVALID_FORMAT(HttpStatus.BAD_GATEWAY, "API-004", "API 응답 형식이 올바르지 않습니다"),
	EXTERNAL_API_CONNECTION_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "API-005", "외부 API 연결 시간이 초과되었습니다"),
	EXTERNAL_API_INVALID_FORMAT(HttpStatus.BAD_GATEWAY, "API-006", "외부 API 응답 형식이 올바르지 않습니다"),
	EXTERNAL_API_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "API-007", "외부 API 서비스를 사용할 수 없습니다"),

	DATABASE_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB-001", "데이터베이스 연결 오류가 발생했습니다"),
	DATABASE_INSERT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DB-002", "데이터베이스 삽입에 실패했습니다"),
	DATABASE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DB-003", "데이터베이스 업데이트에 실패했습니다"),
	DATABASE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DB-004", "데이터베이스 삭제에 실패했습니다"),
	BATCH_INSERT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "DB-005", "배치 데이터 삽입에 실패했습니다"),

	INVALID_COMPANY_CODE(HttpStatus.BAD_REQUEST, "VAL-001", "유효하지 않은 금융회사 코드입니다"),
	INVALID_COMPANY_NAME(HttpStatus.BAD_REQUEST, "VAL-002", "유효하지 않은 금융회사 이름입니다"),
	INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "VAL-003", "유효하지 않은 전화번호 형식입니다"),
	INVALID_HOMEPAGE_URL(HttpStatus.BAD_REQUEST, "VAL-004", "유효하지 않은 홈페이지 URL입니다"),
	REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST, "VAL-005", "필수 입력 항목이 누락되었습니다"),
	INVALID_INTEREST_RATE_RANGE(HttpStatus.BAD_REQUEST, "VAL-006", "유효하지 않은 금리 범위입니다"),
	INVALID_REPAY_TYPE(HttpStatus.BAD_REQUEST, "VAL-007", "유효하지 않은 상환 유형입니다"),
	INVALID_CREDIT_GRADE(HttpStatus.BAD_REQUEST, "VAL-008", "유효하지 않은 신용등급입니다"),
	INVALID_LOAN_RATE_TYPE(HttpStatus.BAD_REQUEST, "VAL-009", "유효하지 않은 대출금리 유형입니다"),
	INVALID_FINANCIAL_COMPANY_CODE(HttpStatus.BAD_REQUEST, "VAL-010", "유효하지 않은 금융회사 코드입니다");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
