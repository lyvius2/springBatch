package com.walter.batch.job;

import com.walter.batch.util.PojoService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class MethodInvokingJobConfig {
	private final PlatformTransactionManager transactionManager;
	private final JobRepository jobRepository;

	public MethodInvokingJobConfig(PlatformTransactionManager transactionManager, JobRepository jobRepository) {
		this.transactionManager = transactionManager;
		this.jobRepository = jobRepository;
	}

	@Bean
	public Job methodInvokingJob() {
		return new JobBuilder("methodInvokingJob", jobRepository).start(methodInvokingStep())
																	   .build();
	}

	@Bean
	public Step methodInvokingStep() {
		return new StepBuilder("methodInvokingStep", jobRepository).tasklet(methodInvokingTasklet(), transactionManager)
																		 .build();
	}

	@Bean
	public MethodInvokingTaskletAdapter methodInvokingTasklet() {
		final MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();
		adapter.setTargetObject(pojoService());
		adapter.setTargetMethod("main");
		return adapter;
	}

	@Bean
	public PojoService pojoService() {
		return new PojoService();
	}
}
