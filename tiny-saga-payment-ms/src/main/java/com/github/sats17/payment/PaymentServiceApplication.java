package com.github.sats17.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.sats17.payment.controller.KafkaController;

@SpringBootApplication
public class PaymentServiceApplication {
	
	@Autowired
	KafkaController kafkaController;

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}
}