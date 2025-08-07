package org.scoula.domain.ledger.service;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.ledger.batch.dto.LedgerVoucherWithEntryDTO;
import org.scoula.domain.ledger.entity.LedgerType;
import org.scoula.domain.remittancegroup.batch.dto.MemberWithInformationDto;
import org.scoula.domain.transaction.entity.TransactionType;
import org.scoula.domain.wallet.dto.request.ChargeWalletRequest;
import org.scoula.domain.wallet.dto.request.TransferToAccountRequest;
import org.scoula.domain.wallet.dto.request.TransferToWalletRequest;

public interface LedgerService {
	void accountForWalletTransfer(TransferToWalletRequest request, String transactionGroupId,
		HttpServletRequest servletRequest);

	void accountForAccountTransfer(TransferToAccountRequest request, String transactionGroupId,
		HttpServletRequest servletRequest);

	void remittanceGroupTransfer(MemberWithInformationDto request, String transactionGroupId);

	void chargeTransfer(ChargeWalletRequest request, String transactionGroupId, HttpServletRequest servletRequest);

	List<LedgerVoucherWithEntryDTO> getLedgerVoucherWithEntryInYesterday();

	BigDecimal getSumByTypes(TransactionType transactionType, LedgerType ledgerType,
		List<LedgerVoucherWithEntryDTO> dtoList);
}
