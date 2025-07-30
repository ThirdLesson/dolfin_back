package org.scoula.domain.financialproduct.financialcompany.controller;

import lombok.RequiredArgsConstructor;

import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;
import org.scoula.domain.financialproduct.financialcompany.service.FinancialCompanyService;
import org.scoula.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/financial-companies")
@RequiredArgsConstructor
public class FinancialCompanyController {

	private final FinancialCompanyService financialCompanyService;

	// API 호출 및 저장
	@PostMapping("/sync")
	public SuccessResponse<List<FinancialCompanyResponseDTO>> fetchAndSaveFinancialCompaniesFromApi() {
		List<FinancialCompanyResponseDTO> savedCompanies = financialCompanyService.fetchAndSaveFinancialCompanies();
		return SuccessResponse.created(savedCompanies);
	}

	// 전체 목록 조회
	@GetMapping
	public SuccessResponse<List<FinancialCompanyResponseDTO>> getAll() {
		List<FinancialCompanyResponseDTO> list = financialCompanyService.getAll();
		return SuccessResponse.ok(list);
	}
}
