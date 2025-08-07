package org.scoula.domain.remittancegroup.batch.scheduler;

import org.scoula.domain.remittancegroup.service.RemittanceService;
import org.springframework.batch.core.Job;
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
public class RemittanceGroupScheduler {

	private final JobLauncher jobLauncher;
	private final Job remittanceGroupJob;
	private final RemittanceService remittanceService;

	public RemittanceGroupScheduler(
		JobLauncher jobLauncher,
		@Qualifier("remittanceGroupJob") Job remittanceGroupJob,
		RemittanceService remittanceService
	) {
		this.jobLauncher = jobLauncher;
		this.remittanceGroupJob = remittanceGroupJob;
		this.remittanceService = remittanceService;
	}

	@Scheduled(cron = "0 0 3  * * *", zone = "Asia/Seoul")
	@SchedulerLock(name = "runRemittanceGroupJobLock", lockAtMostFor = "PT10M") // 락 10분간 유지
	public void runRemittanceGroupJob() {
		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("timestamp", System.currentTimeMillis())
				.toJobParameters();

			jobLauncher.run(remittanceGroupJob, jobParameters);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Scheduled(cron = "0 0 8 * * *", zone = "Asia/Seoul")
	@SchedulerLock(name = "runRemittanceGroupAlarmLock", lockAtMostFor = "PT10M") // 락 10분간 유지
	public void remittanceGroupAlarm() {
		remittanceService.RemittanceGroupAlarm();
	}
}
