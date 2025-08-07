package org.scoula.domain.ledger.batch.scheduler;

import java.time.LocalDateTime;

import org.scoula.global.exception.CustomException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class LedgerValidateBatchScheduler {

	private final Job ledgerValidBatchJob;
	private final JobLauncher jobLauncher;

	public LedgerValidateBatchScheduler(@Qualifier("ledgerValidBatchJob") Job ledgerValidBatchJob,
		JobLauncher jobLauncher) {
		this.ledgerValidBatchJob = ledgerValidBatchJob;
		this.jobLauncher = jobLauncher;
	}

	@Scheduled(cron = "0 0 4  * * *", zone = "Asia/Seoul")
	@SchedulerLock(name = "runRemittanceGroupJobLock", lockAtMostFor = "PT10M") // 락 10분간 유지
	public void runLedgerValidateJob() {
		try {
			log.info("[LedgerValidateBatchScheduler] 배치 시작");

			JobParameters jobParameters = new JobParametersBuilder()
				.addString("datetime", LocalDateTime.now().toString()) // 중복 실행 방지용
				.toJobParameters();

			JobExecution execution = jobLauncher.run(ledgerValidBatchJob, jobParameters);
			log.info("[LedgerValidateBatchScheduler] 배치 종료. 상태: {}", execution.getStatus());

		} catch (CustomException e) {
			log.error("[LedgerValidateBatchScheduler] 배치 실행 중 오류 발생", e);
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("LedgerValidateBatchScheduler 배치 실행 중 시스템 오류 발생", e);
		}
	}
}