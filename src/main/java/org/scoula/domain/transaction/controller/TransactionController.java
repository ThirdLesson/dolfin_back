package org.scoula.domain.transaction.controller;

import java.math.BigDecimal;

import org.scoula.domain.transaction.dto.response.TransactionHistoryResponse;
import org.scoula.domain.transaction.entity.TransactionType;
import org.scoula.domain.transaction.service.TransactionService;
import org.scoula.global.constants.Period;
import org.scoula.global.constants.SortDirection;
import org.scoula.global.response.SuccessResponse;
import org.scoula.global.security.dto.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@Api(tags = "거래 내역 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

	private final TransactionService transactionService;

	@ApiOperation(
		value = "거래 내역 리스트 조회",
		notes = "사용자의 거래 내역을 기간, 유형, 금액 범위로 필터링하고 페이지네이션 및 정렬하여 조회합니다."
	)
	@GetMapping
	public SuccessResponse<Page<TransactionHistoryResponse>> getTransactions(
		@RequestParam(defaultValue = "ONE_MONTH") Period period,
		@RequestParam TransactionType type,
		@RequestParam(required = false) BigDecimal minAmount, @RequestParam(required = false) BigDecimal maxAmount,
		@RequestParam(defaultValue = "LATEST") SortDirection sortDirection,
		@RequestParam int page, @RequestParam int size,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		Page<TransactionHistoryResponse> response = transactionService.getTransactionHistory(period, type, minAmount,
			maxAmount,
			sortDirection, page, size, customUserDetails.getMember());
		return SuccessResponse.ok(response);
	}
}
