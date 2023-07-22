package com.walter.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.SimpleSystemProcessExitCodeMapper;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SystemCommandJobConfig {
	private final PlatformTransactionManager transactionManager;
	private final JobRepository jobRepository;

	public SystemCommandJobConfig(PlatformTransactionManager transactionManager, JobRepository jobRepository) {
		this.transactionManager = transactionManager;
		this.jobRepository = jobRepository;
	}

	@Bean
	public Job systemCommandJob() {
		return new JobBuilder("systemCommandJob", jobRepository).start(systemCommandStep())
																	  .build();
	}

	@Bean
	public Step systemCommandStep() {
		return new StepBuilder("systemCommandStep", jobRepository).tasklet(systemCommandTasklet(), transactionManager)
																	    .build();
	}

	@StepScope
	@Bean
	public SystemCommandTasklet systemCommandTasklet() {
		final SystemCommandTasklet systemCommandTasklet = new SystemCommandTasklet();
		systemCommandTasklet.setCommand("touch tmp.txt");
		systemCommandTasklet.setTimeout(5000);
		systemCommandTasklet.setInterruptOnCancel(true);
		systemCommandTasklet.setSystemProcessExitCodeMapper(touchCodeMapper());
		systemCommandTasklet.setTerminationCheckInterval(5000);
		systemCommandTasklet.setTaskExecutor(new SimpleAsyncTaskExecutor());
		return systemCommandTasklet;
	}

	@Bean
	public SimpleSystemProcessExitCodeMapper touchCodeMapper() {
		return new SimpleSystemProcessExitCodeMapper();
	}
}
