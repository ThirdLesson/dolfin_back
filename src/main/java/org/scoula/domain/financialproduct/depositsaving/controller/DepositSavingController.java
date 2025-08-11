package org.scoula.domain.financialproduct.depositsaving.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags = "예금·적금 상품", description = "예금·적금 상품 조회 및 관리 API")
public class DepositSavingController {

	private final DepositService depositService;
	private final SavingService savingService;

	// 예금 상품 API 호출 및 저장
	@PostMapping("/sync/deposits")
	@ApiOperation(
		value = "외부 API에서 예금상품 데이터 동기화",
		notes = "외부 금융 API에서 최신 예금상품 정보를 가져와서 DB에 저장합니다.")
	@ApiResponses({
		@ApiResponse(code = 204, message = "동기화 완료"),
		@ApiResponse(code = 500, message = "외부 API 호출 실패")})
	public SuccessResponse<Void> syncFromExternalApi() {
		List<Deposit> savedDeposits = depositService.fetchAndSaveDeposits();
		depositService.fetchAndSaveSpclConditions(savedDeposits);
		return SuccessResponse.noContent();
	}

	// 예금 상품 조회 (기간별 필터링)
	@GetMapping("/deposits/recommend")
	@ApiOperation(
		value = "예금상품 추천 목록 조회",
		notes = "기간별, 특별조건별 필터링을 통해 맞춤형 예금상품을 추천합니다. 페이지 번호는 0부터 시작하며, 한 페이지당 20개 상품이 표시됩니다.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "조회 성공"),
		@ApiResponse(code = 400, message = "잘못된 필터 조건"),
		@ApiResponse(code = 401, message = "인증 필요"),
		@ApiResponse(code = 500, message = "서버 오류")})
	public SuccessResponse<PaginatedResponse<DepositsResponse>> getDeposits(
		@ApiParam(value = "상품 기간 필터", example = "TWELVE_MONTHS")
		@RequestParam(required = false) ProductPeriod productPeriod,

		@ApiParam(value = "특별조건 목록 (복수 선택 가능)", example = "[\"HIGH_AMOUNT\", \"ONLINE_ONLY\"]")
		@RequestParam(required = false) List<DepositSpclConditionType> spclConditions,

		@PageableDefault(
			size = 20,
			sort = "maxInterestRate",
			direction = Sort.Direction.DESC
		) Pageable pageable,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		Page<DepositsResponse> page = depositService.getDeposits(productPeriod, spclConditions, pageable,
			customUserDetails.getMember());
		PaginatedResponse<DepositsResponse> response = PaginatedResponse.from(page);
		return SuccessResponse.ok(response);
	}

	// 상품 상세 정보 조회(상품 + 금융회사 정보)
	@GetMapping("/deposits/{depositId}")
	@ApiOperation(value = "예금상품 상세조회")
	public SuccessResponse<DepositsResponse> getProductDetail(
		@ApiParam(value = "예금상품 ID", required = true, example = "1")
		@PathVariable Long depositId) {
		DepositsResponse response = depositService.getDepositDetail(depositId);
		return SuccessResponse.ok(response);
	}

	// 적금 상품 API 호출 및 저장
	@PostMapping("/sync/savings")
	@ApiOperation(
		value = "외부 API에서 적금상품 데이터 동기화",
		notes = "외부 금융 API에서 최신 적금상품 정보를 가져와서 DB에 저장합니다."
	)
	@ApiResponses({
		@ApiResponse(code = 204, message = "동기화 완료"),
		@ApiResponse(code = 500, message = "외부 API 호출 실패")
	})
	public SuccessResponse<Void> syncFromExternalSavingApi() {
		List<Saving> savedSavings = savingService.fetchAndSaveSavings();
		savingService.fetchAndSaveSpclConditions(savedSavings);
		return SuccessResponse.noContent();
	}

	@GetMapping("/savings")
	@ApiOperation(
		value = "전체 적금상품 목록 조회",
		notes = "등록된 모든 적금상품을 페이징하여 조회합니다."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "조회 성공"),
		@ApiResponse(code = 500, message = "서버 오류")
	})
	public SuccessResponse<PaginatedResponse<SavingsResponse>> getSavings(
		@PageableDefault(size = 20, sort = "depositId", direction = Sort.Direction.DESC) Pageable pageable) {
		Page<SavingsResponse> savings = savingService.getAllSavings(pageable);
		return SuccessResponse.ok(PaginatedResponse.from(savings));
	}

	// 적금 상품 조회 (기간별 필터링)
	@GetMapping("/savings/recommend")
	@ApiOperation(
		value = "적금상품 추천 목록 조회",
		notes = "기간별, 특별조건별 필터링을 통해 맞춤형 적금상품을 추천합니다."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "조회 성공"),
		@ApiResponse(code = 400, message = "잘못된 필터 조건"),
		@ApiResponse(code = 401, message = "인증 필요"),
		@ApiResponse(code = 500, message = "서버 오류")
	})
	public SuccessResponse<PaginatedResponse<SavingsResponse>> getSavings(
		@ApiParam(value = "상품 기간 필터", example = "TWENTY_FOUR_MONTHS")
		@RequestParam(required = false) ProductPeriod productPeriod,
		@ApiParam(value = "특별조건 목록 (복수 선택 가능)", example = "[\"YOUNG_ADULT\", \"FIRST_TIME\"]")
		@RequestParam(required = false) List<SavingSpclConditionType> spclConditions,

		@PageableDefault(size = 20, sort = "maxInterestRate", direction = Sort.Direction.DESC)
		Pageable pageable,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		Page<SavingsResponse> page = savingService.getSavings(productPeriod, spclConditions, pageable,
			customUserDetails.getMember());
		PaginatedResponse<SavingsResponse> response = PaginatedResponse.from(page);
		return SuccessResponse.ok(response);
	}

	@GetMapping("/savings/{savingId}")
	@ApiOperation(
		value = "적금상품 상세 조회",
		notes = "특정 적금상품의 상세 정보와 금융회사 정보를 함께 조회합니다."
	)
	@ApiResponses({
		@ApiResponse(code = 200, message = "조회 성공"),
		@ApiResponse(code = 404, message = "상품을 찾을 수 없음"),
		@ApiResponse(code = 500, message = "서버 오류")
	})
	public SuccessResponse<SavingsResponse> getSavingProductDetail(
		@ApiParam(value = "적금상품 ID", required = true, example = "1")
		@PathVariable Long savingId) {
		SavingsResponse response = savingService.getSavingDetail(savingId);
		return SuccessResponse.ok(response);
	}
}
