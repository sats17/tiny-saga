package com.github.sats17.orchestrator.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sats17.orchestrator.OrchestratorException;
import com.github.sats17.orchestrator.configurations.ConfigProperties;
import com.github.sats17.orchestrator.configurations.ServiceEndpoint;
import com.github.sats17.orchestrator.exception.InternalServerException;
import com.github.sats17.orchestrator.model.InventoryMsResponse;
import com.github.sats17.orchestrator.model.KafkaEventRequest;
import com.github.sats17.orchestrator.model.PaymentMsRequest;
import com.github.sats17.orchestrator.model.PaymentMsResponse;
import com.github.sats17.orchestrator.model.ReserveInventoryRequest;
import com.github.sats17.orchestrator.utils.AppUtils;

import reactor.core.publisher.Mono;

@Service
public class OrderService {

	@Autowired
	ServiceEndpoint paymentConfig;

	@Autowired
	ServiceEndpoint inventoryConfig;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	ConfigProperties configProperties;

	public void processOrderInitialization(KafkaEventRequest request) {
		Mono<InventoryMsResponse> paymentMono = processPayment(request).flatMap(paymentMsResponse -> {
			System.out.println("Payment ms response recieved");
			if (paymentMsResponse.getStatus() == 200) {
				return reserveInventory(request);
			}
			return reserveInventory(request);
		});
		// Subscribe invoke payment ms, and inventory ms reactive chain also based on response.
		paymentMono.subscribe();
	}

	private Mono<PaymentMsResponse> processPayment(KafkaEventRequest request) {
		PaymentMsRequest paymentMsRequest = new PaymentMsRequest();
		paymentMsRequest.setOrderId(request.getOrderId());
		paymentMsRequest.setPrice(request.getPrice());
		paymentMsRequest.setUserId(request.getUserId());

		String requestString;
		try {
			requestString = mapper.writeValueAsString(paymentMsRequest);
		} catch (JsonProcessingException e) {
			throw new InternalServerException(e.getMessage());
		}
		ResponseSpec spec = paymentConfig.post(configProperties.getPayment().get("orderPayPath"), requestString);
		return spec.onStatus(httpStatus -> httpStatus.is4xxClientError(), clientResponse -> {
			return clientResponse.bodyToMono(String.class).flatMap(body -> {
				try {
					PaymentMsResponse resp = mapper.readValue(body, PaymentMsResponse.class);
					return Mono.error(new OrchestratorException(resp.getServiceName(),
							clientResponse.statusCode().value(), resp.getResponseMessage()));
				} catch (Exception e) {
					return Mono.error(new OrchestratorException("payment ms", clientResponse.statusCode().value(),
							"Invalid resposne body received from payment ms for 4XX errors"));
				}
			});
		}).bodyToMono(PaymentMsResponse.class);
	}

	private Mono<InventoryMsResponse> reserveInventory(KafkaEventRequest request) {
		System.out.println("Request received for inventory");
		ReserveInventoryRequest reserveInventoryRequest = new ReserveInventoryRequest();
		reserveInventoryRequest.setQuantity(request.getProductQuantity());

		String requestString;
		try {
			requestString = mapper.writeValueAsString(reserveInventoryRequest);
		} catch (JsonProcessingException e) {
			throw new InternalServerException(e.getMessage());
		}
		Map<String, String> params = new HashMap<>();
		params.put("productId", request.getProductId());
		String reserveInventoryPath = AppUtils
				.replacePathParams(configProperties.getInventory().get("reserveInventoryPath"), params);
		ResponseSpec spec = inventoryConfig.put(reserveInventoryPath, requestString);
		return spec.onStatus(httpStatus -> httpStatus.is4xxClientError(), clientResponse -> {
			return clientResponse.bodyToMono(String.class).flatMap(body -> {
				try {
					InventoryMsResponse resp = mapper.readValue(body, InventoryMsResponse.class);
					return Mono.error(new OrchestratorException(resp.getServiceName(),
							clientResponse.statusCode().value(), resp.getResponseMessage()));
				} catch (Exception e) {
					return Mono.error(new OrchestratorException("payment ms", clientResponse.statusCode().value(),
							"Invalid resposne body received from payment ms for 4XX errors"));
				}
			});
		}).bodyToMono(InventoryMsResponse.class);
	}

}
