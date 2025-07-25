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

	// 단건 조회
	FinancialCompany selectFinancialCompanyById(@Param("financialCompanyId") Long id);

	// 등록
	int insertFinancialCompany(FinancialCompany financialCompany);

	// 수정
	int updateFinancialCompany(FinancialCompany financialCompany);

	// 배치 insert(외부 api 데이터 일괄 저장용,중복시 무시)
	int insertFinancialCompanyBatch(@Param("list") List<FinancialCompany> financialCompanies);

	// 금융회사 코드 아이디로 찾기
	String findCodeById(Long financialCompanyId);

	Long findIdByCode(String finCoNo);
}
