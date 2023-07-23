package com.walter.batch.remote.sample;

import com.walter.batch.remote.sample.dto.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(name = "sample", fallbackFactory = SampleClientFallbackFactory.class)
public interface SampleClient {
	@GetMapping(value = "/users")
	List<User> getUsers();

	@PutMapping(value = "/users/{id}")
	User registerUser(@PathVariable("id") long id);
}
