package org.scoula.domain.financialproduct.depositsaving.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.scoula.domain.financialproduct.constants.DepositSpclConditionType;
import org.scoula.domain.financialproduct.constants.ProductPeriod;
import org.scoula.domain.financialproduct.depositsaving.dto.DepositDTO;
import org.scoula.domain.financialproduct.depositsaving.dto.response.DepositsResponse;
import org.scoula.domain.financialproduct.depositsaving.entity.Deposit;
import org.scoula.domain.financialproduct.depositsaving.service.DepositService;
import org.scoula.global.response.SuccessResponse;
import org.scoula.global.security.dto.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deposit-savings")
@RequiredArgsConstructor
@Slf4j
public class DepositSavingController {

	private final DepositService depositService;

	// 예금 상품 API 호출 및 저장
	@PostMapping("/sync/deposits")
	@ApiOperation(value = "예금상품 리스트 저장")
	public SuccessResponse<Void> syncFromExternalApi() {
		List<Deposit> savedDeposits = depositService.fetchAndSaveDeposits();
		depositService.fetchAndSaveSpclConditions(savedDeposits);
		return SuccessResponse.noContent();
	}

	// 예금 상품 조회 (기간별 필터링)
	@GetMapping("/deposits/recommend")
	public SuccessResponse<List<DepositsResponse>> getDeposits(
		@RequestParam(required = false) ProductPeriod productPeriod,
		@RequestParam(required = false) List<DepositSpclConditionType> spclConditions,
		@PageableDefault(
			size = 20,
			sort ="maxInterestRate",
			direction = Sort.Direction.DESC
		) Pageable pageable,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		Page<DepositsResponse> page = depositService.getDeposits(productPeriod, spclConditions, pageable, customUserDetails.getMember());
		List<DepositsResponse> response = page.getContent();
		return SuccessResponse.ok(response);
	}

	// 예금 전체 조회 (최대금리가 내림차순)
	@GetMapping("/deposits/all")
	@ApiOperation(value = "예금상품 리스트 전체조회 - 최대금리 내림차순")
	public SuccessResponse<List<DepositsResponse>> getAllDeposits(
		@PageableDefault(size = 20, sort = "maxInterestRate", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<DepositsResponse> page = depositService.getAllDeposits(pageable);
		return SuccessResponse.ok(page.getContent());
	}

	// 상품 상세 정보 조회(상품 + 금융회사 정보)
	@GetMapping("/deposits/{depositId}")
	@ApiOperation(value = "예금상품 상세조회")
	public SuccessResponse<DepositsResponse> getProductDetail(@PathVariable Long depositId) {
		DepositsResponse response = depositService.getDepositDetail(depositId);
		return SuccessResponse.ok(response);
	}

	// 적금 상품 조회 (기간별 필터링)
	// @GetMapping("/savings")
	// public SuccessResponse<List<SavingsResponse>> getSavings(
	// 	@RequestParam ProductPeriod productPeriod,
	// 	@RequestParam List<SavingSpclCondition> spclConditions,
	// 	Pageable pageable,
	// 	@AuthenticationPrincipal CustomUserDetails customUserDetails) {
	// 	List<SavingsResponse> savings = SavingService.getSavings(productPeriod, spclConditions, pageable,
	// 		customUserDetails.getMember());
	// 	return SuccessResponse.ok(savings);
	// }

}
