package org.scoula.domain.wallet.dto.request;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import org.scoula.domain.account.entity.BankType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "계좌번호로 송금 요청", description = "계좌번호로 송금하기 위한 요청 DTO")
public record TransferToAccountRequest(
	@ApiModelProperty(value = "은행", example = "국민은행", required = true)
	@NotNull(message = "은행은 필수입니다.")
	BankType bankType,

	@ApiModelProperty(value = "계좌번호", example = "46570104048943", required = true)
	@NotBlank(message = "계좌번호는 필수입니다.")
	@Pattern(regexp = "^[0-9]+$", message = "계좌번호는 숫자만 입력해야 합니다.")
	String accountNumber,

	@ApiModelProperty(value = "송금 금액", example = "1000", required = true)
	@NotNull(message = "송금 금액은 필수입니다.")
	@Positive(message = "송금 금액은 0보다 커야 합니다.")
	BigDecimal amount,

	@ApiModelProperty(value = "지갑 비밀번호", example = "1234", required = true)
	@NotNull(message = "지갑 비밀번호는 필수입니다.")
	Integer password
) {
}
