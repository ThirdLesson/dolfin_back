package org.scoula.domain.transaction.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record TransactionHistoryResponse(
	String date,
	List<TransactionResponse> transactions
) {
}
