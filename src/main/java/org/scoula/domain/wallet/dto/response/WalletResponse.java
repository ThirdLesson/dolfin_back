package org.scoula.domain.wallet.dto.response;

import java.math.BigDecimal;

public record WalletResponse(
	Long walletId,
	BigDecimal balance
) {
}
