package org.scoula.domain.financialproduct.financialcompany.service;

import org.scoula.domain.financialproduct.financialcompany.dto.FinancialCompanyDTO;


import java.util.List;

public interface FinancialCompanyService {

    FinancialCompanyDTO getById(Long financialCompanyId);

    List<FinancialCompanyDTO> getAll();

    void fetchAndSaveFinancialCompanies(); // 외부 API 호출 + 저장
}

