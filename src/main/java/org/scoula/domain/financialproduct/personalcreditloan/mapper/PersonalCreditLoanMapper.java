package org.scoula.domain.financialproduct.personalcreditloan.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.financialproduct.personalcreditloan.entity.PersonalCreditLoan;

@Mapper
public interface PersonalCreditLoanMapper {
    
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

    PersonalCreditLoan findById(@Param("id") Long id);
    
    void insert(PersonalCreditLoan loan);
    
    void update(PersonalCreditLoan loan);
    
    void delete(@Param("id") Long id);
}
