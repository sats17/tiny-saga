package com.github.sats17.orchestrator.configurations;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "services")
public class ConfigProperties {

	private Map<String, String> inventory = new HashMap<>();

	private Map<String, String> order = new HashMap<>();

	private Map<String, String> payment = new HashMap<>();

	public Map<String, String> getInventory() {
		return inventory;
	}

	public void setInventory(Map<String, String> inventory) {
		this.inventory = inventory;
	}

	public Map<String, String> getOrder() {
		return order;
	}

	public void setOrder(Map<String, String> order) {
		this.order = order;
	}

	public Map<String, String> getPayment() {
		return payment;
	}

	public void setPayment(Map<String, String> payment) {
		this.payment = payment;
	}

}
