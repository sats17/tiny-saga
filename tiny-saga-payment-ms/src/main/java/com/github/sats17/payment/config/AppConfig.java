package com.github.sats17.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AppConfig {

	@Bean
	public ObjectMapper mapper() {
		return new ObjectMapper();
	}

}
