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

import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.ledger.entity.LedgerCode;
import org.scoula.domain.ledger.entity.LedgerEntry;
import org.scoula.domain.ledger.entity.LedgerVoucher;
import org.scoula.domain.ledger.mapper.LedgerCodeMapper;
import org.scoula.domain.ledger.mapper.LedgerEntryMapper;
import org.scoula.domain.ledger.mapper.LedgerVoucherMapper;
import org.scoula.domain.remittancegroup.batch.dto.MemberWithInformationDto;
import org.scoula.domain.transaction.entity.TransactionType;
import org.scoula.domain.wallet.dto.request.ChargeWalletRequest;
import org.scoula.domain.wallet.dto.request.TransferToAccountRequest;
import org.scoula.domain.wallet.dto.request.TransferToWalletRequest;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LedgerServiceImpl implements LedgerService {

	private final LedgerCodeMapper ledgerCodeMapper;
	private final LedgerVoucherMapper ledgerVoucherMapper;
	private final LedgerEntryMapper ledgerEntryMapper;

	@Override
	public void accountForWalletTransfer(TransferToWalletRequest request, String transactionGroupId,
		HttpServletRequest servletRequest) {
		LedgerVoucher ledgerVoucher = getLedgerVoucher(transactionGroupId, WALLET_TRANSFER);

		List<LedgerEntry> ledgerEntries = new ArrayList<>();

		LedgerCode ledgerCode = ledgerCodeMapper.findByName("BANK_PAYABLE")
			.orElseThrow(() -> new CustomException(LEDGER_CODE_NOT_FOUND, LogLevel.ERROR, null, Common.builder()
				.ledgerCode("BANK_PAYABLE")
				.srcIp(servletRequest.getRemoteAddr())
				.callApiPath(servletRequest.getRequestURI())
				.apiMethod(servletRequest.getMethod())
				.deviceInfo(servletRequest.getHeader("user-agent"))
				.build()));

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
	public void accountForAccountTransfer(TransferToAccountRequest request, String transactionGroupId,
		HttpServletRequest servletRequest) {

		Common common = Common.builder()
			.srcIp(servletRequest.getRemoteAddr())
			.callApiPath(servletRequest.getRequestURI())
			.apiMethod(servletRequest.getMethod())
			.deviceInfo(servletRequest.getHeader("user-agent"))
			.build();

		BigDecimal amount = request.amount();
		LedgerVoucher ledgerVoucher = getLedgerVoucher(transactionGroupId, ACCOUNT_TRANSFER);

		List<LedgerEntry> ledgerEntries = new ArrayList<>();

		LedgerCode bankAsset = ledgerCodeMapper.findByName("BANK_ASSET")
			.orElseThrow(() -> new CustomException(LEDGER_CODE_NOT_FOUND, LogLevel.ERROR, null, common));
		LedgerCode bankPayable = ledgerCodeMapper.findByName("BANK_PAYABLE")
			.orElseThrow(() -> new CustomException(LEDGER_CODE_NOT_FOUND, LogLevel.ERROR, null, common));

		LedgerEntry debitEntry = LedgerEntry.builder()
			.ledgerVoucherId(ledgerVoucher.getLedgerVoucherId())
			.ledgerType(DEBIT)
			.accountCodeId(bankPayable.getAccountCodeId())
			.amount(amount)
			.build();
		ledgerEntries.add(debitEntry);

		LedgerEntry creditEntry = LedgerEntry.builder()
			.ledgerVoucherId(ledgerVoucher.getLedgerVoucherId())
			.ledgerType(CREDIT)
			.accountCodeId(bankAsset.getAccountCodeId())
			.amount(amount)
			.build();
		ledgerEntries.add(creditEntry);

		ledgerEntryMapper.insertBatch(ledgerEntries);
	}

	@Override
	public void remittanceGroupTransfer(MemberWithInformationDto request, String transactionGroupId) {

		BigDecimal amount = request.getAmount();
		LedgerVoucher ledgerVoucher = getLedgerVoucher(transactionGroupId, WITHDRAW);

		List<LedgerEntry> ledgerEntries = new ArrayList<>();

		LedgerCode bankAsset = ledgerCodeMapper.findByName("BANK_ASSET")
			.orElseThrow(
				() -> new CustomException(LEDGER_CODE_NOT_FOUND, LogLevel.ERROR, null, Common.builder().build()));
		LedgerCode bankPayable = ledgerCodeMapper.findByName("BANK_PAYABLE")
			.orElseThrow(
				() -> new CustomException(LEDGER_CODE_NOT_FOUND, LogLevel.ERROR, null, Common.builder().build()));

		LedgerEntry debitEntry = LedgerEntry.builder()
			.ledgerVoucherId(ledgerVoucher.getLedgerVoucherId())
			.ledgerType(DEBIT)
			.accountCodeId(bankPayable.getAccountCodeId())
			.amount(amount)
			.build();
		ledgerEntries.add(debitEntry);

		LedgerEntry creditEntry = LedgerEntry.builder()
			.ledgerVoucherId(ledgerVoucher.getLedgerVoucherId())
			.ledgerType(CREDIT)
			.accountCodeId(bankAsset.getAccountCodeId())
			.amount(amount)
			.build();
		ledgerEntries.add(creditEntry);

		ledgerEntryMapper.insertBatch(ledgerEntries);
	}

	@Override
	public void chargeTransfer(ChargeWalletRequest request, String transactionGroupId,
		HttpServletRequest servletRequest) {

		Common common = Common.builder()
			.srcIp(servletRequest.getRemoteAddr())
			.callApiPath(servletRequest.getRequestURI())
			.apiMethod(servletRequest.getMethod())
			.deviceInfo(servletRequest.getHeader("user-agent"))
			.build();

		BigDecimal amount = request.amount();
		LedgerVoucher ledgerVoucher = getLedgerVoucher(transactionGroupId, CHARGE);

		List<LedgerEntry> ledgerEntries = new ArrayList<>();

		LedgerCode bankAsset = ledgerCodeMapper.findByName("BANK_ASSET")
			.orElseThrow(() -> new CustomException(LEDGER_CODE_NOT_FOUND, LogLevel.ERROR, null, common));
		LedgerCode bankPayable = ledgerCodeMapper.findByName("BANK_PAYABLE")
			.orElseThrow(() -> new CustomException(LEDGER_CODE_NOT_FOUND, LogLevel.ERROR, null, common));

		LedgerEntry debitEntry = LedgerEntry.builder()
			.ledgerVoucherId(ledgerVoucher.getLedgerVoucherId())
			.ledgerType(DEBIT)
			.accountCodeId(bankAsset.getAccountCodeId())
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

	private LedgerVoucher getLedgerVoucher(String transactionGroupId, TransactionType transactionType) {
		String voucherNo = getVoucherNo();

		LedgerVoucher ledgerVoucher = LedgerVoucher.builder()
			.voucherNo(voucherNo)
			.transactionId(transactionGroupId)
			.entryDate(LocalDateTime.now())
			.type(transactionType)
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
