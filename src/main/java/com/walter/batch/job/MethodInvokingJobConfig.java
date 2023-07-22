package com.walter.batch.job;

import com.walter.batch.remote.sample.SampleClient;
import com.walter.batch.util.PojoService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.beans.factory.annotation.Value;
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
		return new StepBuilder("methodInvokingStep", jobRepository).tasklet(methodInvokingTasklet(null), transactionManager)
																		 .build();
	}

	@StepScope
	@Bean
	public MethodInvokingTaskletAdapter methodInvokingTasklet(@Value("#{jobParameters['message']}") String message) {
		final MethodInvokingTaskletAdapter adapter = new MethodInvokingTaskletAdapter();
		adapter.setTargetObject(pojoService());
		adapter.setTargetMethod("main");
		adapter.setArguments(new String[] {message});
		return adapter;
	}

	@Bean
	public PojoService pojoService() {
		return new PojoService();
	}
}
