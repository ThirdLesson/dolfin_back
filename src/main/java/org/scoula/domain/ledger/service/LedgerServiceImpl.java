package org.scoula.domain.ledger.service;

import static org.scoula.domain.ledger.entity.LedgerType.*;
import static org.scoula.domain.transaction.entity.TransactionType.*;
import static org.scoula.domain.wallet.exception.WalletErrorCode.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.scoula.domain.ledger.entity.LedgerCode;
import org.scoula.domain.ledger.entity.LedgerEntry;
import org.scoula.domain.ledger.entity.LedgerVoucher;
import org.scoula.domain.ledger.mapper.LedgerCodeMapper;
import org.scoula.domain.ledger.mapper.LedgerEntryMapper;
import org.scoula.domain.ledger.mapper.LedgerVoucherMapper;
import org.scoula.domain.wallet.dto.request.TransferToAccountRequest;
import org.scoula.domain.wallet.dto.request.TransferToWalletRequest;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LedgerServiceImpl implements LedgerService {

	private final LedgerCodeMapper ledgerCodeMapper;
	private final LedgerVoucherMapper ledgerVoucherMapper;
	private final LedgerEntryMapper ledgerEntryMapper;

	@Override
	@Transactional
	public void accountForWalletTransfer(TransferToWalletRequest request, String transactionGroupId) {
		LedgerVoucher ledgerVoucher = getLedgerVoucher(transactionGroupId);

		List<LedgerEntry> ledgerEntries = new ArrayList<>();

		LedgerCode ledgerCode = ledgerCodeMapper.findByName("WALLET_ASSET")
			.orElseThrow(() -> new CustomException(LEDGER_CODE_NOT_FOUND, LogLevel.ERROR, null, null));

		LedgerEntry debitEntry = LedgerEntry.builder()
			.ledgerVoucherId(ledgerVoucher.getLedgerVoucherId())
			.ledgerType(DEBIT)
			.accountCodeId(ledgerCode.getAccountCodeId())
			.amount(request.amount())
			.build();
		ledgerEntries.add(debitEntry);

		LedgerEntry creditEntry = LedgerEntry.builder()
			.ledgerVoucherId(ledgerVoucher.getLedgerVoucherId())
			.ledgerType(CREDIT)
			.accountCodeId(ledgerCode.getAccountCodeId())
			.amount(request.amount())
			.build();
		ledgerEntries.add(creditEntry);

		ledgerEntryMapper.insertBatch(ledgerEntries);
	}

	@Override
	public void accountForAccountTransfer(TransferToAccountRequest request, String transactionGroupId) {
		BigDecimal amount = request.amount();
		LedgerVoucher ledgerVoucher = getLedgerVoucher(transactionGroupId);

		List<LedgerEntry> ledgerEntries = new ArrayList<>();

		LedgerCode walletAsset = ledgerCodeMapper.findByName("WALLET_ASSET")
			.orElseThrow(() -> new CustomException(LEDGER_CODE_NOT_FOUND, LogLevel.ERROR, null, null));
		LedgerCode bankPayable = ledgerCodeMapper.findByName("BANK_PAYABLE")
			.orElseThrow(() -> new CustomException(LEDGER_CODE_NOT_FOUND, LogLevel.ERROR, null, null));

		LedgerEntry debitEntry = LedgerEntry.builder()
			.ledgerVoucherId(ledgerVoucher.getLedgerVoucherId())
			.ledgerType(DEBIT)
			.accountCodeId(walletAsset.getAccountCodeId())
			.amount(amount)
			.build();
		ledgerEntries.add(debitEntry);

		LedgerEntry creditEntry = LedgerEntry.builder()
			.ledgerVoucherId(ledgerVoucher.getLedgerVoucherId())
			.ledgerType(CREDIT)
			.accountCodeId(bankPayable.getAccountCodeId())
			.amount(amount)
			.build();
		ledgerEntries.add(creditEntry);

		ledgerEntryMapper.insertBatch(ledgerEntries);
	}

	private LedgerVoucher getLedgerVoucher(String transactionGroupId) {
		String voucherNo = getVoucherNo();

		LedgerVoucher ledgerVoucher = LedgerVoucher.builder()
			.voucherNo(voucherNo)
			.transactionId(transactionGroupId)
			.entryDate(LocalDateTime.now())
			.type(TRANSFER)
			.build();
		ledgerVoucherMapper.insert(ledgerVoucher);
		return ledgerVoucher;
	}

	private String getVoucherNo() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String datePrefix = LocalDateTime.now().format(formatter);
		String uuidShortHash = UUID.randomUUID().toString().substring(0, 12);

		return datePrefix + "-" + uuidShortHash;
	}
}
