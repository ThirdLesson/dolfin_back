package org.scoula.domain.financialproduct.financialcompany.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;

import java.util.List;

@Mapper
public interface FinancialCompanyMapper {

    // 1. 전체 조회
    List<FinancialCompany> findAll();

    // 2. ID로 조회
    FinancialCompany findById(@Param("financialCompanyId") Long financialCompanyId);

    // 3. 등록
    void insert(FinancialCompany financialCompany);

    // 4. 수정
    void update(FinancialCompany financialCompany);

    // 5. 삭제
    void delete(@Param("financialCompanyId") Long financialCompanyId);

}
