package org.scoula.domain.transaction.dto.response;

import java.math.BigDecimal;

import org.scoula.domain.transaction.entity.TransactionStatus;
import org.scoula.domain.transaction.entity.TransactionType;

import lombok.Builder;

@Builder
public record TransactionResponse(
	Long transactionId,
	TransactionType type,
	BigDecimal amount,
	String createdAt,
	TransactionStatus status
) {
}
