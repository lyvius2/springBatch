package com.walter.batch.remote.sample;

import com.walter.batch.remote.sample.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SampleClientFallbackFactory implements FallbackFactory<SampleClient> {
	@Override
	public SampleClient create(Throwable cause) {
		return new SampleClient() {
			@Override
			public List<User> getUsers() {
				log.error("Exception at SampleClient.getUsers : {}", cause);
				return List.of();
			}
		};
	}
}
