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

	@ApiModelProperty(value = "환율 숫자", example = "5.3")
	private BigDecimal operator;

	@ApiModelProperty(value = "환율 표시 문자열", example = "1 VND(100) = 5.3 KRW")
	private String exchangeRate;


	@ApiModelProperty(value = "환전 후 금액 표시 문자열", example = "18,867,925 VND")
	private String totalAmount;

}
