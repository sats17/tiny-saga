package com.github.sats17.payment.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/v1/api/payment")
public class KafkaController {
	
	@Value(value = "${spring.kafka.group_id}")
	private String groupId;

	@GetMapping("/healthcheck")
	public String getHealthCheck() {
		return "ok ok health";
	}

	@KafkaListener(topics = { "order-topic" }, groupId = "${spring.kafka.group_id}")
	public void consume(Object taskStatus) {

		System.out.print(String.format("Task status is updated : " + taskStatus));
	}

}
