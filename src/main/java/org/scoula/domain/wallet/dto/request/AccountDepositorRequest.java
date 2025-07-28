package org.scoula.domain.wallet.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.scoula.domain.account.entity.BankType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "예금주명 확인 요청", description = "예금주명을 확인하기 위한 계좌 정보를 담는 요청 DTO")
public record AccountDepositorRequest(
	@ApiModelProperty(value = "은행코드", example = "0004", required = true)
	@NotBlank(message = "은행코드는 필수입니다.")
	BankType bankType,

	@ApiModelProperty(value = "계좌번호", example = "46570104048943", required = true)
	@NotBlank(message = "계좌번호는 필수입니다.")
	@Pattern(regexp = "^[0-9]+$", message = "계좌번호는 숫자만 입력해야 합니다.")
	String accountNumber
) {
}
