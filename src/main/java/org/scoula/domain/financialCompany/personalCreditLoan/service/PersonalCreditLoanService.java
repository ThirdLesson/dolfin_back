package org.scoula.domain.financialCompany.personalCreditLoan.service;

import org.scoula.domain.financialCompany.personalCreditLoan.dto.request.PersonalCreditLoanRequestDTO;
import org.scoula.domain.financialCompany.personalCreditLoan.dto.response.PersonalCreditLoanResponseDTO;

import java.util.List;

public interface PersonalCreditLoanService {

    // 개인신용대출 등록
    void createPersonalCreditLoan(PersonalCreditLoanRequestDTO requestDTO);

    // 개인신용대출 단건 조회
    PersonalCreditLoanResponseDTO getPersonalCreditLoanById(Long personalCreditLoanId);

    // 전체 목록 조회
    List<PersonalCreditLoanResponseDTO> getAllPersonalCreditLoans();

    // 수정
    void updatePersonalCreditLoan(Long personalCreditLoanId, PersonalCreditLoanRequestDTO requestDTO);

    // 삭제
    void deletePersonalCreditLoan(Long personalCreditLoanId);
}
