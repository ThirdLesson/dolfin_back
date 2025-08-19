package org.scoula.domain.exchange.dto.response.exchangeResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel(description = "은행별 환율 정보")
@JsonPropertyOrder({"bankName", "exchangeRate", "totalAmount", "policyList"})
public class BankRateInfo {


	@ApiModelProperty(
		value = "은행명",
		example = "KB국민은행",
		notes = "환율을 제공하는 은행의 정식 명칭",
		required = true,
		position = 1
	)
	private String bankName;

	@ApiModelProperty(
		value = "환율 표시 문자열",
		example = "1,419.4 KRW",
		notes = "사용자에게 표시되는 환율 정보 (읽기 쉬운 형태)",
		required = true,
		position = 2
	)
	private String exchangeRate;

	@JsonIgnore
	@ApiModelProperty(value = "기본 환율", example = "5.3")
	private BigDecimal baseoperation;

	@JsonIgnore
	@ApiModelProperty(value = "타겟 환율", example = "5.3")
	private BigDecimal targetoperation;

	@JsonIgnore
	@ApiModelProperty(value = "최종 금액", example = "18,867,925")
	private BigDecimal totaloperation;

	@ApiModelProperty(
		value = "환전 후 최종 금액",
		example = "1,396.36 USD",
		notes = "수수료와 환율을 모두 적용한 후 실제로 받게 되는 금액 (통화 단위 포함)",
		required = true,
		position = 3
	)
	private String totalAmount;

	@ApiModelProperty(
		value = "창구 환전 후 최종 금액",
		example = "1,396.11 USD",
		notes = "창구에서 금액 (통화 단위 포함)",
		required = true,
		position = 4
	)
	private String changguAmount;

	@ApiModelProperty(
		value = "적용 가능한 우대정책 리스트",
		notes = "해당 은행에서 제공하는 환율 우대정책들을 유리한 순서로 정렬하여 제공합니다. " +
			"각 정책별로 실제 혜택 금액을 계산하여 표시합니다.",
		required = false,
		position = 5
	)
	private List<PolicyResponse> policyList;


	public void addPolicyResponses(List<PolicyResponse> policies) {
		if (this.policyList == null) {
			this.policyList = new ArrayList<>();
		}
		if (policies != null && !policies.isEmpty()) {
			this.policyList.addAll(policies);
		}
	}

	public void addPolicyResponse(PolicyResponse policies) {
		if (this.policyList == null) {
			this.policyList = new ArrayList<>();
		}
		if (policies != null) {
			this.policyList.add(policies);
		}
	}
}
