package org.scoula.domain.financialproduct.financialcompany.service;

import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;

import java.util.List;

public interface FinancialCompanyService {

	// 금융회사 리스트 전체 조회
	List<FinancialCompanyResponseDTO> getAll();

	// API 호출 및 저장
	List<FinancialCompanyResponseDTO> fetchAndSaveFinancialCompanies();

	// 금융회사 아이디로 리스트 저장.
	FinancialCompany getById(Long financialCompanyId);
}
