package org.scoula.domain.financialproduct.depositsaving.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.scoula.domain.financialproduct.constants.DepositSpclConditionType;
import org.scoula.domain.financialproduct.constants.ProductPeriod;
import org.scoula.domain.financialproduct.constants.SavingSpclConditionType;
import org.scoula.domain.financialproduct.depositsaving.dto.response.DepositsResponse;
import org.scoula.domain.financialproduct.depositsaving.dto.response.SavingsResponse;
import org.scoula.domain.financialproduct.depositsaving.entity.Deposit;
import org.scoula.domain.financialproduct.depositsaving.entity.Saving;
import org.scoula.domain.financialproduct.depositsaving.service.DepositService;
import org.scoula.domain.financialproduct.depositsaving.service.SavingService;
import org.scoula.domain.financialproduct.page.PaginatedResponse;
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
	private final SavingService savingService;

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
	@ApiOperation(value = "예금상품 리스트 필터링 조회", notes = "page 변수에 0,1,2 등등 넣어서 페이징 하세요 한 페이지는 20개 입니다.")
	public SuccessResponse<PaginatedResponse<DepositsResponse>> getDeposits(
		@RequestParam(required = false) ProductPeriod productPeriod,
		@RequestParam(required = false) List<DepositSpclConditionType> spclConditions,
		@PageableDefault(
			size = 20,
			sort ="maxInterestRate",
			direction = Sort.Direction.DESC
		) Pageable pageable,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		Page<DepositsResponse> page = depositService.getDeposits(productPeriod, spclConditions, pageable, customUserDetails.getMember());
		PaginatedResponse<DepositsResponse> response = PaginatedResponse.from(page);
		return SuccessResponse.ok(response);
	}

	// 상품 상세 정보 조회(상품 + 금융회사 정보)
	@GetMapping("/deposits/{depositId}")
	@ApiOperation(value = "예금상품 상세조회")
	public SuccessResponse<DepositsResponse> getProductDetail(@PathVariable Long depositId) {
		DepositsResponse response = depositService.getDepositDetail(depositId);
		return SuccessResponse.ok(response);
	}

	// 적금 상품 API 호출 및 저장
	@PostMapping("/sync/savings")
	@ApiOperation(value = "적금상품 리스트 저장")
	public SuccessResponse<Void> syncFromExternalSavingApi(){
		List<Saving> savedSavings = savingService.fetchAndSaveSavings();
		savingService.fetchAndSaveSpclConditions(savedSavings);
		return SuccessResponse.noContent();
	}

	// 적금 상품 조회 (기간별 필터링)
	@GetMapping("/savings/recommend")
	@ApiOperation(value = "적금상품 리스트 필터링 조회")
	public SuccessResponse<PaginatedResponse<SavingsResponse>> getSavings(
		@RequestParam(required = false) ProductPeriod productPeriod,
		@RequestParam(required = false) List<SavingSpclConditionType> spclConditions,
		@PageableDefault(size = 20,sort = "maxInterestRate",direction = Sort.Direction.DESC)
		Pageable pageable,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		Page<SavingsResponse> page = savingService.getSavings(productPeriod,spclConditions,pageable,customUserDetails.getMember());
		PaginatedResponse<SavingsResponse> response = PaginatedResponse.from(page);
		return SuccessResponse.ok(response);
	}

	@GetMapping("/savings/{savingId}")
	@ApiOperation(value = "적금상품 상세조회")
	public SuccessResponse<SavingsResponse> getSavingProductDetail(@PathVariable Long savingId) {
		SavingsResponse response = savingService.getSavingDetail(savingId);
		return SuccessResponse.ok(response);
	}
}
