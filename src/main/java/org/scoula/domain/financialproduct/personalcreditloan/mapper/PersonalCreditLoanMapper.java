package org.scoula.domain.financialproduct.personalcreditloan.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.financialproduct.personalcreditloan.entity.PersonalCreditLoan;

@Mapper
public interface PersonalCreditLoanMapper {
    
    // 조건에 맞는 대출상품 조회
    List<PersonalCreditLoan> findLoansByConditions(
        @Param("filterType") String filterType,
        @Param("minAmount") Integer minAmount,
        @Param("maxAmount") Integer maxAmount,
        @Param("offset") int offset,
        @Param("limit") int limit
    );

    int countLoansByConditions(
        @Param("minAmount") Integer minAmount,
        @Param("maxAmount") Integer maxAmount
    );

    // ID로 상세 조회
    PersonalCreditLoan findById(@Param("id") Long id);
    
    // 신규 등록
    void insert(PersonalCreditLoan loan);
    
    // 수정
    void update(PersonalCreditLoan loan);
    
    // 삭제
    void delete(@Param("id") Long id);
}
