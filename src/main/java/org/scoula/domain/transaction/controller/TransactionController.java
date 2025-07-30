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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {

	private final TransactionService transactionService;

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
