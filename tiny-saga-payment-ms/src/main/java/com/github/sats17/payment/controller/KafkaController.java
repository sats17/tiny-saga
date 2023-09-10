package com.github.sats17.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sats17.payment.model.KafkaEventRequest;


@RestController
@RequestMapping("/v1/api/payment")
public class KafkaController {
	
	@Value(value = "${spring.kafka.group_id}")
	private String groupId;

	@GetMapping("/healthcheck")
	public String getHealthCheck() {
		return "ok ok health";
	}
	
	@Autowired
	ObjectMapper mapper;

	@KafkaListener(topics = { "order-topic" }, groupId = "${spring.kafka.group_id}")
	public void consume(String event) throws InterruptedException {
		KafkaEventRequest eventObj = null;
		try {
			eventObj = mapper.readValue(event, KafkaEventRequest.class);
			System.out.println(eventObj.toString());
			switch(eventObj.getEventName()) {
			case ORDER_INITIATED:
				processOrderInitatedEvent(eventObj);
			case INVENTORY_INSUFFICIENT:
				processInventoryInsufficientEvent(eventObj);
			default:
				System.out.println("Invalid event received");
				break;
			}
		} catch (Exception e) {
			System.out.println("Something went wrong in event => "+event);
			System.out.println(e.getMessage());
		}
	}

	private void processOrderInitatedEvent(KafkaEventRequest event) {
		System.out.println("Processing order intiated event");
	}
	
	private void processInventoryInsufficientEvent(KafkaEventRequest event) {
		System.out.println("Processing inventory insufficient event");
	}
	
}
