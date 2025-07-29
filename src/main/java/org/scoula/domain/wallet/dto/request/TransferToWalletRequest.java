package org.scoula.domain.wallet.dto.request;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "지갑으로 송금 요청", description = "지갑으로 송금하기 위한 요청 DTO")
public record TransferToWalletRequest(
	@NotNull(message = "수신자 핸드폰 번호는 필수입니다.")
	String phoneNumber,

	@NotNull(message = "송금 금액은 필수입니다.")
	@Positive(message = "송금 금액은 0보다 커야 합니다.")
	BigDecimal amount,

	@NotNull(message = "지갑 비밀번호는 필수입니다.")
	Integer password
) {
}
