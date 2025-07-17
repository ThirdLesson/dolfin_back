package org.scoula.domain.financialproduct.personalcreditLoan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.domain.financialproduct.personalcreditLoan.entity.PersonalCreditLoan;

import java.util.List;

@Mapper
public interface PersonalCreditLoanMapper {

    void insertPersonalCreditLoan(PersonalCreditLoan personalCreditLoan);

    PersonalCreditLoan selectPersonalCreditLoanById(Long personalCreditLoanId);

    List<PersonalCreditLoan> selectAllPersonalCreditLoans();

    void updatePersonalCreditLoan(PersonalCreditLoan personalCreditLoan);

    void deletePersonalCreditLoan(Long personalCreditLoanId);
}
