package org.scoula.domain.financialproduct.financialcompany.controller;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.scoula.domain.financialproduct.financialcompany.dto.FinancialCompanyDTO;
import org.scoula.domain.financialproduct.financialcompany.service.FinancialCompanyService;
import org.scoula.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/financial-companies")
@RequiredArgsConstructor
public class FinancialCompanyController {

    private final FinancialCompanyService financialCompanyService;

    // 외부 API 호출 및 저장
    @PostMapping("/sync")
    public SuccessResponse<Void> syncFromExternal() {
        financialCompanyService.fetchAndSaveFinancialCompanies();
        return SuccessResponse.noContent();
    }

    // 전체 목록 조회
    @GetMapping
    public SuccessResponse<List<FinancialCompanyDTO>> getAll() {
        List<FinancialCompanyDTO> list = financialCompanyService.getAll();
        return SuccessResponse.ok(list);
    }

    // 단건 조회
    @GetMapping("/{id}")
    public SuccessResponse<FinancialCompanyDTO> getById(@PathVariable Long id) {
        return SuccessResponse.ok(financialCompanyService.getById(id));
    }
}

