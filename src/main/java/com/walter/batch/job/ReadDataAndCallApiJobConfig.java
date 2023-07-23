package com.walter.batch.job;

import com.walter.batch.data.entity.UserEntity;
import com.walter.batch.data.repository.UserRepository;
import com.walter.batch.remote.sample.SampleClient;
import com.walter.batch.remote.sample.dto.User;
import com.walter.batch.util.RandomChunkSizePolicy;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
public class ReadDataAndCallApiJobConfig {
	private final PlatformTransactionManager transactionManager;
	private final JobRepository jobRepository;
	private final UserRepository userRepository;
	private final SampleClient sampleClient;

	public ReadDataAndCallApiJobConfig(PlatformTransactionManager transactionManager, JobRepository jobRepository, UserRepository userRepository, SampleClient sampleClient) {
		this.transactionManager = transactionManager;
		this.jobRepository = jobRepository;
		this.userRepository = userRepository;
		this.sampleClient = sampleClient;
	}

	@Bean
	public Job readDataAndCallJob() {
		return new JobBuilder("readDataAndCallApiJob", jobRepository).start(readDataAndCallStep())
																		   .incrementer(new RunIdIncrementer())
																		   .build();
	}

	@Bean
	public Step readDataAndCallStep() {
		return new StepBuilder("readDataAndCallApiStep", jobRepository).<UserEntity, UserEntity>chunk(randomChunkSizePolicy(), transactionManager)
																			 .reader(userItemReader())
																			 .writer(userItemWriter())
																			 .build();
	}

	@Bean
	public ListItemReader<UserEntity> userItemReader() {
		final List<UserEntity> users = userRepository.findAll();
		return new ListItemReader<>(users);
	}

	@Bean
	public ItemWriter<UserEntity> userItemWriter() {
		return users -> {
			for (UserEntity user : users) {
				final User returnMessage = sampleClient.registerUser(user.getId());
				System.out.println("Registered ID : " + user.getId() + ", " + user.getName());
			}
		};
	}

	@Bean
	public RandomChunkSizePolicy randomChunkSizePolicy() {
		return new RandomChunkSizePolicy();
	}
}
