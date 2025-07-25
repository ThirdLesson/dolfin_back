package org.scoula.domain.codef.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.scoula.domain.account.entity.BankType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "사용자 계좌 조회 및 1원 인증에 필요한 객체")
public record CodefConnectedIdRequest(
	@ApiModelProperty(notes = "은행명", example = "국민은행", required = true)
	@NotNull(message = "은행명은 필수입니다.")
	BankType bankType,
	@ApiModelProperty(notes = "계좌번호", example = "94320200955935", required = true)
	@NotBlank(message = "계좌번호는 필수입니다.")
	String accountNumber,
	@ApiModelProperty(notes = "은행 웹 아이디", example = "YUYJ1998", required = true)
	@NotBlank(message = "은행 웹 아이디는 필수입니다.")
	String bankId,
	@ApiModelProperty(notes = "은행 웹 비밀번호", example = "iuoo856963*", required = true)
	@NotBlank(message = "은행 웹 비밀번호는 필수입니다.")
	String bankPassword
) {
}
