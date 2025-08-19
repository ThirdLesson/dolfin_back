package org.scoula.domain.financialproduct.financialcompany.service;

import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;

import java.util.List;

public interface FinancialCompanyService {

	List<FinancialCompanyResponseDTO> getAll();

	List<FinancialCompanyResponseDTO> fetchAndSaveFinancialCompanies();

	FinancialCompany getById(Long financialCompanyId);
}
