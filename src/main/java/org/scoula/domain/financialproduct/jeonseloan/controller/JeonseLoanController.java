package org.scoula.domain.financialproduct.jeonseloan.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.scoula.domain.financialproduct.constants.JeonseLoanRateType;
import org.scoula.domain.financialproduct.jeonseloan.dto.request.JeonseLoanRequestDTO;
import org.scoula.domain.financialproduct.jeonseloan.dto.response.JeonseLoanDetailResponseDTO;
import org.scoula.domain.financialproduct.jeonseloan.dto.response.JeonseLoanResponseDTO;
import org.scoula.domain.financialproduct.jeonseloan.service.JeonseLoanService;
import org.scoula.domain.financialproduct.page.PaginatedResponse;
import org.scoula.domain.member.entity.Member;
import org.scoula.global.response.SuccessResponse;
import org.scoula.global.security.dto.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jeonse-loan")
@RequiredArgsConstructor
@Slf4j
public class JeonseLoanController {

	private final JeonseLoanService jeonseLoanService;

	@GetMapping("/recommend")
	@ApiOperation(value = "전세대출상품 리스트 필터링 조회", notes = "금리 기준 정렬 및 금액 범위 필터링 ")
	public SuccessResponse<PaginatedResponse<JeonseLoanResponseDTO>> getJeonseLoans(
		@RequestParam(required = false) JeonseLoanRateType sortBy,
		@RequestParam(required = false) Long minAmount,
		@RequestParam(required = false) Long maxAmount,
		@PageableDefault(
			size = 20,
			sort = "maxInterestRate",
			direction = Sort.Direction.DESC
		) Pageable pageable) {
		JeonseLoanRequestDTO request = new JeonseLoanRequestDTO(sortBy, minAmount, maxAmount);
		Page<JeonseLoanResponseDTO> page = jeonseLoanService.getAllJeonseLoans(request,pageable);
		PaginatedResponse<JeonseLoanResponseDTO> response = PaginatedResponse.from(page);
		return SuccessResponse.ok(response);
	}

	// 전세자금대출 단건 조회
	@GetMapping("/{jeonseLoanId}")
	@ApiOperation(value = "전세대출상품 리스트 단건 조회")
	public SuccessResponse<JeonseLoanDetailResponseDTO> getJeonseLoanDetail(
		@PathVariable Long jeonseLoanId,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		System.out.println("전세대출 상품 상세 조회 - ID: " + jeonseLoanId + ", 사용자: " + customUserDetails.getUsername());

		Member member = customUserDetails != null ? customUserDetails.getMember() : null;
		JeonseLoanDetailResponseDTO jeonseLoan = jeonseLoanService.getJeonseLoanDetail(jeonseLoanId,
			member);
		return SuccessResponse.ok(jeonseLoan);
	}
}
