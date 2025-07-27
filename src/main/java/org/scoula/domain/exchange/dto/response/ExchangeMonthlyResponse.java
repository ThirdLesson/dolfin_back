package org.scoula.domain.exchange.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel(description = "최근 1달간의 환율 정보 응답")
public class ExchangeMonthlyResponse {


	@ApiModelProperty(value = "대상 통화", example = "USD")
	private String targetExchange; // 대상 통화 (예: "USD")
	@ApiModelProperty(value = "환율 값", example = "1400.50")
	private BigDecimal exchangeValue; // 환율 값 (예: "1400.50")
	@ApiModelProperty(value = "환율 적용 날짜", example = "2023-10-01")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate exchangeDate; // 환율 적용 날짜 (예: "2023-10-01")


}
