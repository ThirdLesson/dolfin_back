package org.scoula.domain.exchange.dto.response.exchangeResponse;

import java.math.BigDecimal;
import java.util.List;

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

	@ApiModelProperty(value = "환율 표시 문자열", example = "1 VND(100) = 5.3 KRW")
	private String exchangeRate;


	@ApiModelProperty(value = "기본 환율", example = "5.3")
	private BigDecimal baseoperation;

	@ApiModelProperty(value = "타겟 환율", example = "5.3")
	private BigDecimal targetoperation;

	@ApiModelProperty(value = "환전 후 금액 표시 문자열", example = "18,867,925 VND")
	private String totalAmount;

	@ApiModelProperty(value = "우대 정책들", example = "정책1, 정책2")
	private List<PolicyResponse> policyList; // 모든 정책 옵션들

	public void addPolicyResponse(List<PolicyResponse> policies) {
		this.policyList.addAll(policies);
	}
}
