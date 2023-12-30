package com.github.sats17.orchestrator.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ServiceEndpointConfig {

	@Autowired
	ConfigProperties configProperties;

	@Bean
	ServiceEndpoint paymentConfig() {
		String host = configProperties.getPayment().get("host");
		String port = configProperties.getPayment().get("port");
		String protocol = configProperties.getPayment().get("protocol");
		return new ServiceEndpoint(WebClient.create(protocol + "://" + host + ":" + port));
	}

}
