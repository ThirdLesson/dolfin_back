package org.scoula.domain.transaction.dto.response;

import java.math.BigDecimal;

import org.scoula.domain.transaction.entity.TransactionStatus;
import org.scoula.domain.transaction.entity.TransactionType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;

@Builder
@ApiModel(description = "단일 거래 내역 상세 응답")
public record TransactionResponse(
	@ApiModelProperty(value = "거래 ID", example = "12345")
	Long transactionId,

	@ApiModelProperty(value = "거래 유형", example = "WALLET_TRANSFER", allowableValues = "CHARGE,WALLET_TRANSFER,ACCOUNT_TRANSFER,DUTCH_PAY")
	TransactionType type,

	@ApiModelProperty(value = "거래 금액", example = "50000")
	BigDecimal amount,

	@ApiModelProperty(value = "거래 발생 시각 (YYYY-MM-DD HH:MM:SS)", example = "2025-07-30 13:30:15")
	String createdAt,

	@ApiModelProperty(value = "거래 상태", example = "SUCCESS", allowableValues = "PENDING,SUCCESS,FAILED")
	TransactionStatus status
) {
}
