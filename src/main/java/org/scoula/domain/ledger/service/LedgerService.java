package org.scoula.domain.ledger.service;

import org.scoula.domain.wallet.dto.request.ChargeWalletRequest;
import org.scoula.domain.wallet.dto.request.TransferToAccountRequest;
import org.scoula.domain.wallet.dto.request.TransferToWalletRequest;

public interface LedgerService {
	void accountForWalletTransfer(TransferToWalletRequest request, String transactionGroupId);

	void accountForAccountTransfer(TransferToAccountRequest request, String transactionGroupId);

	void chargeTransfer(ChargeWalletRequest request, String transactionGroupId);

}
