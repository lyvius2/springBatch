package com.walter.batch.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class HelloWorldJobLazyConfig {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	public HelloWorldJobLazyConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
	}

	@Bean
	public Job job() {
		return new JobBuilder("helloWorldLazyJob", jobRepository).incrementer(new RunIdIncrementer())
																	   .start(step1())
																	   .build();
	}

	@JobScope
	@Bean
	public Step step1() {
		return new StepBuilder("step1", jobRepository).tasklet(helloWorldLazyTasklet(null), transactionManager)
															.build();
	}

	@StepScope
	@Bean
	public Tasklet helloWorldLazyTasklet(@Value("#{jobParameters['name']}") String name) {
		return ((contribution, chunkContext) -> {
			log.info("Hello, {}", name);
			return RepeatStatus.FINISHED;
		});
	}
}
