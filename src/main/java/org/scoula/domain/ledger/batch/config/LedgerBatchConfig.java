package org.scoula.domain.ledger.batch.config;

import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.scoula.domain.ledger.batch.reader.LedgerIdRangePartitioner;
import org.scoula.domain.transaction.entity.Transaction;
import org.scoula.domain.transaction.mapper.TransactionMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class LedgerBatchConfig {

	private final MyBatisPagingItemReader<Transaction> transactionReader;
	private final ItemWriter<Transaction> transactionWriter;
	private final TransactionMapper transactionMapper;

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final StepBuilderFactory stepBuilderFactory;
	private final TaskExecutor taskExecutor;

	private final int threadSize = 1;

	@Bean
	public Job ledgerValidBatchJob() {
		return new JobBuilder("ledgerValidBatchJob")
			.start(partitionedLedgerValidBatchStep())
			.repository(jobRepository)
			.build();
	}

	@Bean
	public Step partitionedLedgerValidBatchStep() {
		return stepBuilderFactory.get("partitionedLedgerValidBatchStep")
			.partitioner("ledgerValidBatchStep", new LedgerIdRangePartitioner(transactionMapper))
			.step(ledgerValidBatchStep())
			.gridSize(threadSize)
			.transactionManager(transactionManager)
			.taskExecutor(taskExecutor)
			.build();
	}

	@Bean
	public Step ledgerValidBatchStep() {
		return stepBuilderFactory.get("ledgerValidBatchStep")
			.<Transaction, Transaction>chunk(1000000)
			.reader(transactionReader)
			.writer(transactionWriter)
			.repository(jobRepository)
			.transactionManager(transactionManager)
			.build();
	}
}
