package com.walter.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class HelloWorldJobConfig {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	public HelloWorldJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
	}

	@Bean
	public Job helloWorldJob() {
		return new JobBuilder("helloWorldJob", jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(helloWorldStep())
				.build();
	}

	@JobScope
	@Bean
	public Step helloWorldStep() {
		return new StepBuilder("helloWorldStep", jobRepository)
				.tasklet(helloWorldTasklet(), transactionManager)
				.build();
	}

	@StepScope
	@Bean
	public Tasklet helloWorldTasklet() {
		return (contribution, chunkContext) -> {
			final String nameParameter = (String) chunkContext.getStepContext()
															  .getJobParameters()
															  .get("name");
			System.out.println(String.format("Hello World! Spring Batch -----> %s", nameParameter));
			return RepeatStatus.FINISHED;
		};
	}
}
