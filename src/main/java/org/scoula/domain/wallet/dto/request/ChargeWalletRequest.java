package org.scoula.domain.wallet.dto.request;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "전자지갑 충전시 사용 dto")
public record ChargeWalletRequest(
	@ApiModelProperty(
		value = "충전하려는 금액",
		example = "200000",
		required = true,
		position = 1
	)
	@NotNull(message = "충전 금액 입력은 필수입니다.")
	BigDecimal amount,
	@ApiModelProperty(
		value = "출금 계좌 번호",
		example = "3561358392423",
		required = true,
		position = 2
	)
	@NotBlank(message = "계좌번호 입력은 필수 입니다.")
	String accountNumber,
	@ApiModelProperty(
		value = "전자지갑 비밀번호",
		example = "1234",
		required = true,
		position = 3
	)
	@NotNull(message = "전자지갑 비밀번호는 필수입니다.")
	Integer walletPassword
) {
}
