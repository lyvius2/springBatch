package com.walter.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class HelloWorld implements Tasklet {
	private static final String HELLO_WORLD = "Hello, %s";

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		final String name = (String) chunkContext.getStepContext()
												 .getJobParameters()
												 .get("name");
		final ExecutionContext jobContext = chunkContext.getStepContext()
														.getStepExecution()
														.getJobExecution()
														.getExecutionContext();
		jobContext.put("user.name", name);
		System.out.printf((HELLO_WORLD) + "%n", name);
		return RepeatStatus.FINISHED;
	}
}
