package org.scoula.domain.ledger.batch.writer;

import static org.scoula.domain.ledger.exception.LedgerErrorCode.*;
import static org.scoula.domain.transaction.entity.TransactionType.*;

import java.math.BigDecimal;
import java.util.List;

import org.scoula.domain.ledger.batch.dto.LedgerVoucherWithEntryDTO;
import org.scoula.domain.ledger.entity.LedgerType;
import org.scoula.domain.ledger.service.LedgerService;
import org.scoula.domain.transaction.entity.Transaction;
import org.scoula.domain.transaction.entity.TransactionType;
import org.scoula.domain.transaction.service.TransactionService;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class LedgerWriter {

	private final TransactionService transactionService;
	private final LedgerService ledgerService;

	@Bean
	@StepScope
	public ItemWriter<Transaction> LedgerItemWriter() {

		return transactions -> {
			List<LedgerVoucherWithEntryDTO> ledgerVoucherWithEntryInYesterday = ledgerService.getLedgerVoucherWithEntryInYesterday();

			checkOfTransactionTypeBalanced((List<Transaction>)transactions, ledgerVoucherWithEntryInYesterday, CHARGE);
			checkOfTransactionTypeBalanced((List<Transaction>)transactions, ledgerVoucherWithEntryInYesterday,
				ACCOUNT_TRANSFER);
			checkOfTransactionTypeBalanced((List<Transaction>)transactions, ledgerVoucherWithEntryInYesterday,
				WALLET_TRANSFER);
			checkOfTransactionTypeBalanced((List<Transaction>)transactions, ledgerVoucherWithEntryInYesterday, DEPOSIT);
			checkOfTransactionTypeBalanced((List<Transaction>)transactions, ledgerVoucherWithEntryInYesterday,
				WITHDRAW);
		};
	}

	private void checkOfTransactionTypeBalanced(List<Transaction> transactions,
		List<LedgerVoucherWithEntryDTO> ledgerVoucherWithEntryInYesterday, TransactionType transactionType) {
		BigDecimal sumOfTransaction = transactionService.getSumByTransactionType(transactionType, transactions);

		BigDecimal sumOfLedgerDebit = ledgerService.getSumByTypes(transactionType, LedgerType.DEBIT,
			ledgerVoucherWithEntryInYesterday);
		BigDecimal sumOfLedgerCredit = ledgerService.getSumByTypes(transactionType, LedgerType.CREDIT,
			ledgerVoucherWithEntryInYesterday);

		boolean isMatch =
			sumOfTransaction.compareTo(sumOfLedgerDebit) == 0 &&
				sumOfTransaction.compareTo(sumOfLedgerCredit) == 0
				&& sumOfLedgerDebit.compareTo(sumOfLedgerCredit) == 0;

		if (!isMatch) {
			throw new CustomException(LEDGER_BALANCED_BROKEN, LogLevel.ERROR, null, Common.builder().build(),
				transactionType.name() + " 타입 정합성 검증 실패, 거래내역 합: " + sumOfTransaction
					+ "\nDEBIT TYPE 합: " + sumOfLedgerDebit
					+ "\nCREDIT TYPE 합: " + sumOfLedgerCredit);
		} else {
			log.info(
				transactionType.name() + " 타입 정합성 검증 성공, 거래내역 합: " + sumOfTransaction + "\nDEBIT TYPE 합: "
					+ sumOfLedgerDebit + "\nCREDIT TYPE 합: " + sumOfLedgerCredit);
		}
	}
}
