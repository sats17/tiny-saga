package com.github.sats17.orchestrator.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Creates new webclient against each microservices
 */
@Configuration
public class ServiceEndpointConfig {
	
	private static final String HOST = "host";
	private static final String PORT = "port";
	private static final String PROTOCOL = "protocol";

	@Autowired
	ConfigProperties configProperties;

	@Bean
	ServiceEndpoint paymentConfig() {
		String host = configProperties.getPayment().get(HOST);
		String port = configProperties.getPayment().get(PORT);
		String protocol = configProperties.getPayment().get(PROTOCOL);
		return new ServiceEndpoint(WebClient.create(protocol + "://" + host + ":" + port));
	}
	
	@Bean
	ServiceEndpoint inventoryConfig() {
		String host = configProperties.getInventory().get(HOST);
		String port = configProperties.getInventory().get(PORT);
		String protocol = configProperties.getInventory().get(PROTOCOL);
		return new ServiceEndpoint(WebClient.create(protocol + "://" + host + ":" + port));
	}
	
	@Bean
	ServiceEndpoint orderConfig() {
		String host = configProperties.getOrder().get(HOST);
		String port = configProperties.getOrder().get(PORT);
		String protocol = configProperties.getOrder().get(PROTOCOL);
		return new ServiceEndpoint(WebClient.create(protocol + "://" + host + ":" + port));
	}

}
