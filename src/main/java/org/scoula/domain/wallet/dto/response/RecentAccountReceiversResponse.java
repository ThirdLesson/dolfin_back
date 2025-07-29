package org.scoula.domain.wallet.dto.response;

import org.scoula.domain.account.entity.BankType;

public record RecentAccountReceiversResponse(
	String name,
	BankType bankType,
	String accountNumber
) {
}
