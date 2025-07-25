package org.scoula.domain.financialproduct.personalcreditloan.service;

import org.scoula.domain.financialproduct.personalcreditloan.dto.PersonalCreditLoanDTO;

import java.util.List;

public interface PersonalCreditLoanService {

	// 개인신용대출 등록
	void createPersonalCreditLoan(PersonalCreditLoanDTO personalCreditLoanDTO);

	// 개인신용대출 단건 조회
	PersonalCreditLoanDTO getPersonalCreditLoanById(Long personalCreditLoanId);

	// 전체 목록 조회
	List<PersonalCreditLoanDTO> getAllPersonalCreditLoans();

	// 수정
	void updatePersonalCreditLoan(Long personalCreditLoanId, PersonalCreditLoanDTO personalCreditLoanDTO);

	// 금융회사별 개인신용대출 상품 조회
	List<PersonalCreditLoanDTO> getPersonalCreditLoansByFinancialCompanyId(Long financialCompanyId);

	// 대출상품유형별 조회
	List<PersonalCreditLoanDTO> getPersonalCreditLoansByCrdtPrdtTypeNm(String crdtPrdtTypeNm);

	// 평균금리 기준 정렬 조회 (낮은 금리순)
	List<PersonalCreditLoanDTO> getPersonalCreditLoansOrderByAvgRate();

	// API 데이터 동기화 (외부 API에서 데이터를 가져와 DB에 저장/갱신)
	void synchronizePersonalCreditLoanData();

	// 배치 저장
	void savePersonalCreditLoansBatch(List<PersonalCreditLoanDTO> personalCreditLoanDtos);

}
