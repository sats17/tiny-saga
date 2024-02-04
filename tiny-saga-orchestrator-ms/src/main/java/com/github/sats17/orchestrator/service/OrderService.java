package com.github.sats17.orchestrator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sats17.orchestrator.configurations.ConfigProperties;
import com.github.sats17.orchestrator.configurations.ServiceEndpoint;
import com.github.sats17.orchestrator.model.KafkaEventRequest;
import com.github.sats17.orchestrator.model.PaymentMsRequest;
import com.github.sats17.orchestrator.model.PaymentMsResponse;
import com.github.sats17.orchestrator.utils.AppUtils;

import reactor.core.publisher.Mono;

@Service
public class OrderService {

	@Autowired
	ServiceEndpoint paymentConfig;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	ConfigProperties configProperties;

	public void processOrderInitialization(KafkaEventRequest request) {

	}

	private PaymentMsResponse processPayment(KafkaEventRequest request) throws JsonProcessingException {
		PaymentMsRequest paymentMsRequest = new PaymentMsRequest();
		paymentMsRequest.setOrderId(request.getOrderId());
		paymentMsRequest.setPrice(request.getPrice());
		paymentMsRequest.setUserId(request.getUserId());

		String requestString = mapper.writeValueAsString(paymentMsRequest);
		String url = AppUtils.buildUrl(configProperties.getPayment().get("protocol"),
				configProperties.getPayment().get("host"), configProperties.getPayment().get("port"));
		Mono<String> response = paymentConfig.post(url, requestString);
		// Learn webflux for how to wrap response with mono, so based on mono resposne inventory can be called.
		// All should be without blocking.
		return null;

	}

}
