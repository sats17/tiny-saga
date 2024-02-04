package com.github.sats17.orchestrator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.PriorityOrdered;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sats17.orchestrator.OrchestratorException;
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

	public void processOrderInitialization(KafkaEventRequest request) throws JsonProcessingException {
		Mono<PaymentMsResponse> paymentMono = processPayment(request);
		paymentMono.flatMap(paymentMsResponse -> {
			if (paymentMsResponse.getStatus() == 200) {
				try {
					return processPayment(request);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
			return paymentMono;
		});
	}

	private Mono<PaymentMsResponse> processPayment(KafkaEventRequest request) throws JsonProcessingException {
		PaymentMsRequest paymentMsRequest = new PaymentMsRequest();
		paymentMsRequest.setOrderId(request.getOrderId());
		paymentMsRequest.setPrice(request.getPrice());
		paymentMsRequest.setUserId(request.getUserId());

		String requestString = mapper.writeValueAsString(paymentMsRequest);
		String url = AppUtils.buildUrl(configProperties.getPayment().get("protocol"),
				configProperties.getPayment().get("host"), configProperties.getPayment().get("port"));
		ResponseSpec spec = paymentConfig.post(url, requestString);
		return spec.onStatus(httpStatus -> httpStatus.is4xxClientError(), clientResponse -> {
			return clientResponse.bodyToMono(String.class).flatMap(body -> {
				try {
					PaymentMsResponse resp = mapper.readValue(body, PaymentMsResponse.class);
					return Mono.error(new OrchestratorException(resp.getServiceName(), clientResponse.statusCode().value(),
							resp.getResponseMessage()));
				} catch (Exception e) {
					return Mono.error(new OrchestratorException("payment ms", clientResponse.statusCode().value(),
							"Invalid resposne body received from payment ms for 4XX errors"));
				}
			});
		}).bodyToMono(PaymentMsResponse.class);

	}

}
