package com.walter.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ConditionalJobConfig {
	private final PlatformTransactionManager transactionManager;
	private final JobRepository jobRepository;

	public ConditionalJobConfig(PlatformTransactionManager transactionManager, JobRepository jobRepository) {
		this.transactionManager = transactionManager;
		this.jobRepository = jobRepository;
	}

	@Bean
	public Job conditionalJob() {
		return new JobBuilder("conditionalJob", jobRepository).start(firstStep())
																	.on("FAILED").to(failureStep())
																	.from(firstStep()).on("*").to(successStep())
																	.end()
																	.build();
	}

	@Bean
	public Step firstStep() {
		return new StepBuilder("firstStep", jobRepository).tasklet(passTasklet(), transactionManager).build();
	}

	@Bean
	public Step successStep() {
		return new StepBuilder("successStep", jobRepository).tasklet(successTasklet(), transactionManager).build();
	}

	@Bean
	public Step failureStep() {
		return new StepBuilder("failureStep", jobRepository).tasklet(failTasklet(), transactionManager).build();
	}

	@Bean
	public Tasklet passTasklet() {
		return ((contribution, chunkContext) -> {
			//return RepeatStatus.FINISHED;
			throw new RuntimeException("This is a failure.");
		});
	}

	@Bean
	public Tasklet successTasklet() {
		return ((contribution, chunkContext) -> {
			System.out.println("Success! ");
			return RepeatStatus.FINISHED;
		});
	}

	@Bean
	public Tasklet failTasklet() {
		return ((contribution, chunkContext) -> {
			System.out.println("Failure!");
			return RepeatStatus.FINISHED;
		});
	}
}
