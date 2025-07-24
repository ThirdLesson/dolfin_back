package org.scoula.domain.exchange.dto.response;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel(description = "은행별 환율 정보")
public class BankRateInfo {

	@ApiModelProperty(value = "은행명", example = "KB국민은행")
	private String bankName;

	@ApiModelProperty(value = "환율 (1 목표통화당 KRW)", example = "0.053")
	private BigDecimal exchangeRate;

	@ApiModelProperty(value = "환율 표시 문자열", example = "1 VND(100) = 5.3 KRW")
	private String rateDisplay;

	@ApiModelProperty(value = "환전 후 받게 될 목표 통화 금액", example = "18867924.53")
	private BigDecimal totalAmount;

	@ApiModelProperty(value = "환전 후 금액 표시 문자열", example = "18,867,925 VND")
	private String totalAmountDisplay;

}
