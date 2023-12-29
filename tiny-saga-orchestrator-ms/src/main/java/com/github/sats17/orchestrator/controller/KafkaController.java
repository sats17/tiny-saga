package com.github.sats17.orchestrator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sats17.orchestrator.model.KafkaEventRequest;
import com.github.sats17.orchestrator.utils.AppUtils;

@Component
public class KafkaController {

	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Value(value = "${spring.kafka.group_id}")
	private String groupId;
	
	@KafkaListener(topics = { "orchestrator-topic" }, groupId = "${spring.kafka.group_id}")
	public void consume(String event) throws InterruptedException {
		KafkaEventRequest eventObj = null;
		try {
			eventObj = mapper.readValue(event, KafkaEventRequest.class);
			AppUtils.printLog("Event recevied = "+eventObj.getEventName());
			switch (eventObj.getEventName()) {
			case ORDER_INITIATED:
				AppUtils.printLog("ORDER_INITIATED Event not supported");
				break;
			default:
				AppUtils.printLog("UNKNOWN Event not supported");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
			System.out.println("Something went wrong in event => " + event);
			System.out.println(e.getMessage());
		}
	}

	
}
