package com.walter.batch.job;

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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
public class DefaultChunkJobConfig {
	private final PlatformTransactionManager transactionManager;
	private final JobRepository jobRepository;

	public DefaultChunkJobConfig(PlatformTransactionManager transactionManager, JobRepository jobRepository) {
		this.transactionManager = transactionManager;
		this.jobRepository = jobRepository;
	}

	@Bean
	public Job chunkJob() {
		return new JobBuilder("chunkJob", jobRepository).incrementer(new RunIdIncrementer())
															  .start(chunkStep())
															  .build();
	}

	@Bean
	public Step chunkStep() {
		return new StepBuilder("chunkStep", jobRepository).<String, String>chunk(1000, transactionManager)
																.reader(itemReader())
																.writer(itemWriter())
																.build();
	}

	@Bean
	public ListItemReader<String> itemReader() {
		List<String> items = new ArrayList<>(100000);
		for (int index = 0; index < 100000; index++) {
			items.add(UUID.randomUUID().toString());
		}
		return new ListItemReader<>(items);
	}

	@Bean
	public ItemWriter<String> itemWriter() {
		return items -> {
			for (String item : items) {
				System.out.println(">> current item : " + item);
			}
		};
	}
}
