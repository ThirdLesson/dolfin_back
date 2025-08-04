package org.scoula.domain.financialproduct.financialcompany.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;

import java.util.List;
import java.util.Map;

@Mapper
public interface FinancialCompanyMapper {

	// 전체 조회
	List<FinancialCompany> selectAllFinancialCompany();

	// 금융회사 코드로 조회
	FinancialCompany selectByCode(String code);

	// 여러 금융회사 데이터르 한 번에 저장
	int insertBatch(List<FinancialCompany> newCompanies);

	// 금융회사 ID 조회
	FinancialCompany selectById(Long financialCompanyId);
}
