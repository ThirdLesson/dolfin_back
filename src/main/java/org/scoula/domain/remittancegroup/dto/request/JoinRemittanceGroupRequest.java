package org.scoula.domain.remittancegroup.dto.request;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.scoula.domain.remmitanceinformation.entity.IntermediaryBankCommission;
import org.scoula.global.constants.Currency;

import io.swagger.annotations.ApiModelProperty;

public record JoinRemittanceGroupRequest(
	@ApiModelProperty(
		value = "가입하려는 통화",
		example = "USD",
		required = true
	)
	@NotNull(message = "가입하려는 통화는 필수로 입력하셔야 합니다.")
	Currency currency,
	@ApiModelProperty(
		value = "송금 목적",
		example = "기러기 아빠",
		required = true
	)
	@NotNull(message = "송금 목적은 필수로 입력하셔야 합니다.")
	String purpose,

	@ApiModelProperty(
		value = "송금 금액",
		example = "200000",
		required = true
	)
	@NotNull(message = "송금 금액은 필수로 입력하셔야 합니다.")
	@DecimalMin(value = "100000", inclusive = true, message = "송금 금액은 최소 10만원 이상이어야 합니다.")
	@DecimalMax(value = "5000000", inclusive = true, message = "송금 금액은 최대 500만원 이하여야 합니다.")
	BigDecimal amount,
	@ApiModelProperty(
		value = "송금 날짜",
		example = "13",
		required = true
	)
	@NotNull(message = "송금 날짜는 필수로 입력하셔야 합니다.")
	Integer remittanceDate,
	@ApiModelProperty(
		value = "Swift Code",
		example = "SHBKKRSE"
	)
	@NotBlank(message = "스위프트 코드는 필수로 입력하셔야 합니다.")
	String swiftCode,
	@ApiModelProperty(
		value = "수취인 계좌 번호",
		example = "3561358392423",
		required = true
	)
	@NotBlank(message = "수취인 계좌 번호는 필수로 입력하셔야 합니다.")
	String receiverAccount,
	@ApiModelProperty(
		value = "수취인 은행 명",
		example = "KB",
		required = true
	)
	@NotBlank(message = "수취인 은행명은 필수로 입력하셔야 합니다.")
	String receiverBank,
	@ApiModelProperty(
		value = "수취인 이름",
		example = "이준범",
		required = true
	)
	@NotBlank(message = "수취인 이름은 필수로 입력하셔야 합니다.")
	String receiverName,
	@ApiModelProperty(
		value = "수취인 주소명",
		example = "고덕로 360",
		required = true
	)
	@NotBlank(message = "수취인 주소는 필수로 입력하셔야 합니다.")
	String receiverAddress,
	@ApiModelProperty(
		value = "Router Code",
		example = "통화가 USD 일 때 입력해주시면 됩니다."
	)
	String routerCode,
	@ApiModelProperty(
		value = "중계 수수료 부담 방식",
		example = "OUR, SHA, BEN",
		required = true
	)
	@NotNull(message = "중계 수수료 부담 방식은 필수로 입력하셔야 합니다.")
	IntermediaryBankCommission intermediaryBankCommission
) {
}
