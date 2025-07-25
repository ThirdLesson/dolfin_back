package org.scoula.domain.account.dto.request;

import org.scoula.domain.account.entity.BankType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountRequest {
	Long walletId;
	Long memberId;
	String accountNumber;
	BankType bankType;
}
