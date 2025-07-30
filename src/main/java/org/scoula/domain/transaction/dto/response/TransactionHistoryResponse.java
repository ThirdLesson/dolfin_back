package org.scoula.domain.transaction.dto.response;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;

@Builder
@ApiModel(description = "날짜별 그룹화된 거래 내역 응답")
public record TransactionHistoryResponse(
	@ApiModelProperty(value = "거래 발생 날짜 (YYYY-MM-DD)", example = "2025-07-30")
	String date,
	@ApiModelProperty(value = "해당 날짜의 거래 내역 리스트")
	List<TransactionResponse> transactions
) {
}
