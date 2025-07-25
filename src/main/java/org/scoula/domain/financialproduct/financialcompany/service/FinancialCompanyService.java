package org.scoula.domain.financialproduct.financialcompany.service;

import org.scoula.domain.financialproduct.financialcompany.dto.request.FinancialCompanyRequestDTO;
import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;

import java.util.List;

public interface FinancialCompanyService {

    void create(FinancialCompanyRequestDTO requestDTO);

    FinancialCompanyResponseDTO getById(Long financialCompanyId);

    List<FinancialCompanyResponseDTO> getAll();

    void update(Long id, FinancialCompanyRequestDTO requestDTO);

    void delete(Long financialCompanyId);
}
