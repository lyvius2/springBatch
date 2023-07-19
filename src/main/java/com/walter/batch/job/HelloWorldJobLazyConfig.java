package com.walter.batch.job;

import com.walter.batch.util.JobLoggerListener;
import com.walter.batch.util.ParameterValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
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

import java.util.Arrays;

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
		return new JobBuilder("helloWorldLazyJob", jobRepository).start(step1())
																	   .validator(validator())
																	   .incrementer(new RunIdIncrementer())
																	   .listener(new JobLoggerListener())
																	   .build();
	}

	@JobScope
	@Bean
	public Step step1() {
		return new StepBuilder("step1", jobRepository).tasklet(helloWorldLazyTasklet(null, null), transactionManager)
															.build();
	}

	@StepScope
	@Bean
	public Tasklet helloWorldLazyTasklet(@Value("#{jobParameters['name']}") String name,
	                                     @Value("#{jobParameters['fileName']}") String fileName) {
		return ((contribution, chunkContext) -> {
			log.info("Hello, {}", name);
			log.info("fileName = {}", fileName);
			return RepeatStatus.FINISHED;
		});
	}

	@Bean
	public CompositeJobParametersValidator validator() {
		final CompositeJobParametersValidator validator = new CompositeJobParametersValidator();

		final DefaultJobParametersValidator defaultJobParametersValidator = new DefaultJobParametersValidator(new String[]{"fileName"}, new String[]{"name", "run.id"});
		defaultJobParametersValidator.afterPropertiesSet();
		validator.setValidators(Arrays.asList(new ParameterValidator(), defaultJobParametersValidator));
		return validator;
	}
}
