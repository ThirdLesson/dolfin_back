package org.scoula.domain.financialproduct.personalcreditloan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.financialproduct.personalcreditloan.entity.PersonalCreditLoan;

import java.util.List;

@Mapper
public interface PersonalCreditLoanMapper {

    void insertPersonalCreditLoan(PersonalCreditLoan personalCreditLoan);

    PersonalCreditLoan selectPersonalCreditLoanById(Long personalCreditLoanId);

    List<PersonalCreditLoan> selectAllPersonalCreditLoans();

    void updatePersonalCreditLoan(PersonalCreditLoan personalCreditLoan);

//    금융회사별 개인신용대출 상품 조회
    List<PersonalCreditLoan> selectPersonalCreditLoansByFinancialCompanyId(Long financialCompanyId);

//   특정 금융회사의 특정 상품명으로 조회 (중복 확인)
    PersonalCreditLoan selectPersonalCreditLoanByFinancialCompanyIdAndName(
            @Param("financialCompanyId") Long financialCompanyId,
            @Param("name") String name);

//    대출상품유형별 조회
    List<PersonalCreditLoan> selectPersonalCreditLoansByCrdtPrdtTypeNm(String crdtPrdtTypeNm);

//   평균금리 기준 정렬 조회 (낮은 금리순)
    List<PersonalCreditLoan> selectPersonalCreditLoansOrderByAvgRate();

//   배치 삽입 (API 데이터 일괄 저장용)
    void insertPersonalCreditLoansBatch(List<PersonalCreditLoan> personalCreditLoans);

}
