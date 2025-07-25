package org.scoula.domain.financialproduct.errorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.scoula.global.exception.errorCode.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FinancialErrorCode implements ErrorCode {

	// ===== API 관련 오류 =====
	DEPOSIT_SAVING_API_CALL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "D-001", "금융회사 API 호출 또는 저장 중 오류 발생"),
	JEONSE_LOAN_API_CALL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "J-001", "전세자금대출 API 호출 또는 저장 중 오류 발생"),
	API_RESPONSE_PARSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "D-002", "API 응답 데이터 파싱 실패"),
	API_CONNECTION_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "D-003", "API 연결 시간 초과"),
	API_RESPONSE_EMPTY(HttpStatus.BAD_GATEWAY, "D-004", "API 응답이 비어있거나 올바르지 않습니다"),
	API_RESPONSE_INVALID_FORMAT(HttpStatus.BAD_GATEWAY, "D-005", "API 응답 형식이 올바르지 않습니다"),
	API_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "D-006", "외부 API 서비스를 사용할 수 없습니다"),

	// ===== 데이터 관련 오류 =====
	FINANCIAL_COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "D-101", "해당 금융회사를 찾을 수 없습니다"),
	INVALID_FINANCIAL_COMPANY_DATA(HttpStatus.BAD_REQUEST, "D-102", "유효하지 않은 금융회사 데이터"),
	DUPLICATE_FINANCIAL_COMPANY(HttpStatus.CONFLICT, "D-103", "이미 존재하는 금융회사"),
	DEPOSIT_SAVING_NOT_FOUND(HttpStatus.NOT_FOUND, "D-104", "해당 예금/적금 상품을 찾을 수 없습니다"),
	JEONSE_LOAN_NOT_FOUND(HttpStatus.NOT_FOUND, "D-105", "해당 전세자금대출 상품을 찾을 수 없습니다"),
	INVALID_DEPOSIT_SAVING_TYPE(HttpStatus.BAD_REQUEST, "D-106", "올바르지 않은 예금/적금 상품 타입입니다"),
	INVALID_FINANCIAL_GROUP_CODE(HttpStatus.BAD_REQUEST, "D-107", "올바르지 않은 금융기관 그룹 코드입니다"),
	PRODUCT_DATA_INCOMPLETE(HttpStatus.BAD_REQUEST, "D-108", "상품 데이터가 불완전합니다"),
	EMPTY_JEONSE_LOAN_LIST(HttpStatus.BAD_REQUEST, "D-109", "저장할 전세대출 데이터가 비어있습니다"),
	INVALID_INTEREST_RATE_RANGE(HttpStatus.BAD_REQUEST, "D-110", "올바르지 않은 금리 범위입니다"),
	DUPLICATE_PersonalCredit_Loan(HttpStatus.CONFLICT, "D-111", "이미 존재하는 개인신용대출 상품입니다"),
	PERSONAL_LOAN_NOT_FOUND(HttpStatus.NOT_FOUND, "D-112", "해당 개인신용대출 상품을 찾을 수 없습니다"),

	// ===== JSON 처리 관련 오류 =====
	JSON_PARSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "D-108", "JSON 데이터 파싱에 실패했습니다"),
	INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "D-109", "JSON 형식이 올바르지 않습니다"),

	// ===== DB 관련 오류 =====
	DATABASE_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "D-201", "데이터베이스 연결 오류"),
	DATA_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "D-202", "데이터 저장 실패"),
	DATABASE_INSERT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "D-203", "데이터베이스 삽입 실패"),
	DATABASE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "D-204", "데이터베이스 업데이트 실패"),
	BATCH_INSERT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "D-205", "배치 데이터 삽입 실패"),
	JEONSE_LOAN_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "J-201", "전세대출 데이터 저장 실패"),
	JEONSE_LOAN_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "J-202", "전세대출 데이터 업데이트 실패"),
	JEONSE_LOAN_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "J-203", "전세대출 데이터 삭제 실패"),
	JEONSE_LOAN_BATCH_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "J-204", "전세대출 배치 저장 실패");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
