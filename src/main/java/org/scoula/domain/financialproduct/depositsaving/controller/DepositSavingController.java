package org.scoula.domain.financialproduct.depositsaving.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.scoula.domain.financialproduct.constants.DepositSpclCondition;
import org.scoula.domain.financialproduct.constants.ProductPeriod;
import org.scoula.domain.financialproduct.depositsaving.dto.response.DepositsResponse;
import org.scoula.domain.financialproduct.depositsaving.service.DepositService;
import org.scoula.global.response.SuccessResponse;
import org.scoula.global.security.dto.CustomUserDetails;
import org.springframework.data.domain.Pageable;
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
	public SuccessResponse<Void> syncFromExternalApi() {
		depositService.fetchAndSaveDepositSaving();
		return SuccessResponse.noContent();
	}

	// 예금 상품 조회 (기간별 필터링)
	@GetMapping("/deposits")
	public SuccessResponse<List<DepositsResponse>> getDeposits(
		@RequestParam ProductPeriod productPeriod,
		@RequestParam List<DepositSpclCondition> spclConditions,
		Pageable pageable,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		List<DepositsResponse> response = (List<DepositsResponse>)depositService.getDeposits(productPeriod,
			spclConditions, pageable,
			customUserDetails.getMember());
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

	// // 상품 상세 정보 조회(상품 + 금융회사 정보)
	// @GetMapping("/{id}")
	// public SuccessResponse<DepositSavingResponseDTO> getProductDetail(@PathVariable Long id) {
	// 	DepositSavingResponseDTO productDetail = depositSavingService.getProductDetail(id);
	// 	productDetail.getProduct().getName());
	// 	return SuccessResponse.ok(productDetail);
	// }
}
