package org.scoula.domain.financialproduct.financialcompany.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;
import org.scoula.domain.financialproduct.financialcompany.service.FinancialCompanyService;
import org.scoula.global.response.SuccessResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/financial-companies")
@RequiredArgsConstructor
@Api(tags = "금융회사", description = "금융회사 관련 API")
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
	@ApiOperation(
		value = "전체 금융회사 목록 조회",
		notes = "시스템에 등록된 모든 금융회사의 목록을 조회합니다."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "조회 성공", response = FinancialCompanyResponseDTO.class, responseContainer = "List"),
		@ApiResponse(code = 500, message = "서버 오류")
	})
	public SuccessResponse<List<FinancialCompanyResponseDTO>> getAll() {
		List<FinancialCompanyResponseDTO> list = financialCompanyService.getAll();
		return SuccessResponse.ok(list);
	}
}
