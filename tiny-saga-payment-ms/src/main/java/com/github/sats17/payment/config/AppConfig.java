package com.github.sats17.payment.config;

import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AppConfig {

	@Bean("fixedThreadPool")
	public ExecutorService fixedThreadPool() {
		return Executors.newFixedThreadPool(5);
	}

	@Bean("singleThreaded")
	public ExecutorService singleThreadedExecutor() {
		return Executors.newSingleThreadExecutor();
	}

	@Bean("cachedThreadPool")
	public ExecutorService cachedThreadPool() {
		return Executors.newCachedThreadPool();
	}

	@Bean("worksteelThreadPool")
	public ExecutorService worksteelThreadPool() {
		return Executors.newWorkStealingPool();
	}

	@Bean("scheduledThreadPool")
	public ExecutorService scheduledThreadPool() {
		return Executors.newScheduledThreadPool(10);
	}

	@Bean
	public ObjectMapper mapper() {
		return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public HttpClient httpClient() {
		// You can customize the HttpClient configuration as needed
		return HttpClient.newHttpClient();
	}

}
