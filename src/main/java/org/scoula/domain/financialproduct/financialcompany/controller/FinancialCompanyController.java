package org.scoula.domain.financialproduct.financialcompany.controller;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;
import org.scoula.domain.financialproduct.financialcompany.service.FinancialCompanyService;
import org.scoula.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/financial-companies")
@RequiredArgsConstructor
@ApiModel(description = "금융회사 API")
public class FinancialCompanyController {

	private final FinancialCompanyService financialCompanyService;

	// API 호출 및 저장
	@PostMapping("/sync")
	@ApiOperation(value = "전체 금융회사 리스트 저장")
	public SuccessResponse<List<FinancialCompanyResponseDTO>> fetchAndSaveFinancialCompaniesFromApi() {
		List<FinancialCompanyResponseDTO> savedCompanies = financialCompanyService.fetchAndSaveFinancialCompanies();
		return SuccessResponse.created(savedCompanies);
	}

	// 전체 목록 조회
	@GetMapping
	@ApiOperation(value = "전체 금융회사 리스트 조회")
	@ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = FinancialCompanyResponseDTO.class, responseContainer = "List")})
	public SuccessResponse<List<FinancialCompanyResponseDTO>> getAll() {
		List<FinancialCompanyResponseDTO> list = financialCompanyService.getAll();
		return SuccessResponse.ok(list);
	}
}
