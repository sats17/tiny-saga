package com.github.sats17.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.sats17.payment.controller.KafkaController;

@SpringBootApplication
public class PaymentServiceApplication {
	
	@Autowired
	KafkaController kafkaController;
	
	@Value(value = "${spring.kafka.group_id}")
	private String groupId;


	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}
}