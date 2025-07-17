package org.scoula.domain.financialCompany.personalCreditLoan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.scoula.domain.financialCompany.personalCreditLoan.entity.PersonalCreditLoan;

import java.util.List;

@Mapper
public interface PersonalCreditLoanMapper {

    void insertPersonalCreditLoan(PersonalCreditLoan personalCreditLoan);

    PersonalCreditLoan selectPersonalCreditLoanById(Long personalCreditLoanId);

    List<PersonalCreditLoan> selectAllPersonalCreditLoans();

    void updatePersonalCreditLoan(PersonalCreditLoan personalCreditLoan);

    void deletePersonalCreditLoan(Long personalCreditLoanId);
}
