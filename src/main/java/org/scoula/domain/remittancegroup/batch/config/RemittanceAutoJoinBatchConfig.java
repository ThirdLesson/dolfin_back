package org.scoula.domain.remittancegroup.batch.config;

import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.scoula.domain.remittancegroup.batch.reader.IdRangePartitioner;
import org.scoula.domain.remittancegroup.entity.RemittanceGroup;
import org.scoula.domain.remittancegroup.mapper.RemittanceGroupMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class RemittanceAutoJoinBatchConfig {

	private final RemittanceGroupMapper remittanceGroupMapper;
	private final MyBatisPagingItemReader<RemittanceGroup> remittanceGroupReader;
	private final ItemWriter<RemittanceGroup> remittanceGroupWriter;

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final StepBuilderFactory stepBuilderFactory;
	private final TaskExecutor taskExecutor;

	public RemittanceAutoJoinBatchConfig(RemittanceGroupMapper remittanceGroupMapper,
		@Qualifier("autoJoinReader")
		MyBatisPagingItemReader<RemittanceGroup> remittanceGroupReader,
		@Qualifier("remittanceGroupAutoJoinItemWriter") ItemWriter<RemittanceGroup> remittanceGroupWriter,
		JobRepository jobRepository, PlatformTransactionManager transactionManager,
		StepBuilderFactory stepBuilderFactory,
		TaskExecutor taskExecutor) {
		this.remittanceGroupMapper = remittanceGroupMapper;
		this.remittanceGroupReader = remittanceGroupReader;
		this.remittanceGroupWriter = remittanceGroupWriter;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.stepBuilderFactory = stepBuilderFactory;
		this.taskExecutor = taskExecutor;
	}

	private final int threadSize = 3;

	@Bean
	public Job remittanceGroupAutoJoinJob() {
		return new JobBuilder("remittanceGroupAutoJoinJob")
			.start(partitionedRemittanceGroupAutoJoinStep())
			.repository(jobRepository)
			.build();
	}

	@Bean
	public Step partitionedRemittanceGroupAutoJoinStep() {
		return stepBuilderFactory.get("partitionedRemittanceGroupAutoJoinStep")
			.partitioner("remittanceGroupAutoJoinStep", new IdRangePartitioner(remittanceGroupMapper))
			.step(remittanceGroupAutoJoinStep())
			.gridSize(threadSize)
			.transactionManager(transactionManager)
			.taskExecutor(taskExecutor)
			.build();
	}

	@Bean
	public Step remittanceGroupAutoJoinStep() {
		return stepBuilderFactory.get("remittanceGroupAutoJoinStep")
			.<RemittanceGroup, RemittanceGroup>chunk(1000)
			.reader(remittanceGroupReader)
			.writer(remittanceGroupWriter)
			.repository(jobRepository)
			.transactionManager(transactionManager)
			.build();
	}
}
