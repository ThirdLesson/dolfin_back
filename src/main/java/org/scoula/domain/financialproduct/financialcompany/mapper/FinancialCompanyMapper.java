package org.scoula.domain.financialproduct.financialcompany.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;

import java.util.List;
import java.util.Map;

@Mapper
public interface FinancialCompanyMapper {

	List<FinancialCompany> selectAllFinancialCompany();

	FinancialCompany selectByCode(String code);

	int insertBatch(List<FinancialCompany> newCompanies);

	FinancialCompany selectById(Long financialCompanyId);
}
