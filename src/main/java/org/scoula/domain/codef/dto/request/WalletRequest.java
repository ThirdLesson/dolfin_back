package org.scoula.domain.codef.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.scoula.domain.account.entity.BankType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ApiModel(description = "전자지갑 또는 계좌 등록에 필요한 객체")
public class WalletRequest {
	@ApiModelProperty(notes = "무시하셔도 됩니다.", example = "", required = false)
	Long walletId;
	@ApiModelProperty(notes = "전자지갑 비밀번호로 미이 전자지갑이 존재하는 경우에는 안 보내셔도 됩니다.", example = "1234", required = true)
	String walletPassword;
	@ApiModelProperty(notes = "계좌번호", example = "94320200955935", required = true)
	@NotBlank(message = "계좌번호는 필수입니다.")
	String accountNumber;
	@ApiModelProperty(notes = "은행명", example = "국민은행", required = true)
	@NotNull(message = "은행명은 필수입니다.")
	BankType bankType;
}
