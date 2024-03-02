package com.github.sats17.orchestrator.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sats17.orchestrator.configurations.ConfigProperties;
import com.github.sats17.orchestrator.configurations.Enums.OrchestratorOrderStatus;
import com.github.sats17.orchestrator.configurations.ServiceEndpoint;
import com.github.sats17.orchestrator.exception.InternalServerException;
import com.github.sats17.orchestrator.exception.ServiceException;
import com.github.sats17.orchestrator.model.InventoryMsResponse;
import com.github.sats17.orchestrator.model.KafkaEventRequest;
import com.github.sats17.orchestrator.model.OrderMsResponse;
import com.github.sats17.orchestrator.model.PaymentMsRequest;
import com.github.sats17.orchestrator.model.PaymentMsResponse;
import com.github.sats17.orchestrator.model.ReserveInventoryRequest;
import com.github.sats17.orchestrator.model.UpdateOrderStatusRequest;
import com.github.sats17.orchestrator.utils.AppUtils;

import reactor.core.publisher.Mono;

@Service
public class OrderService {

	@Autowired
	ServiceEndpoint paymentConfig;

	@Autowired
	ServiceEndpoint inventoryConfig;

	@Autowired
	ServiceEndpoint orderConfig;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	ConfigProperties configProperties;

	// Fix logic for how to send order status.
	// We are doing two time status validation for each ms response. how to avoid
	// that ?
	// Solution : Do not validate http response code in response body. Always check
	// http status code.
	// Based on http error status code decide what to do with order.
	public void processOrder(KafkaEventRequest request) {
		Mono<Object> paymentMono = processPayment(request).flatMap(paymentMsResponse -> {
			AppUtils.printLog("Success response recieved from payment, will process inventory");
			return reserveInventory(request).flatMap(inventoryMsResponse -> updateOrderStatus(request.getOrderId(),
					OrchestratorOrderStatus.INVENTORY_RESERVERVED, null));
		});

		// Subscribe method invoke payment ms mono as well as all mono/flux chain that
		// present inside flatmap.
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
		return spec
				.onStatus(httpStatus -> httpStatus.isSameCodeAs(HttpStatus.NOT_FOUND),
						clientResponse -> handlePaymentUserNotFound(clientResponse, request, "paymentMs"))
				.onStatus(httpStatus -> httpStatus.isSameCodeAs(HttpStatus.BAD_REQUEST),
						clientResponse -> handleAuthorizationError(clientResponse, request, "paymentMs"))
				.onStatus(httpStatus -> httpStatus.is5xxServerError(),
						clientResponse -> handle5XXError(clientResponse, request, "paymentMs"))
				.bodyToMono(PaymentMsResponse.class);
	}

	private Mono<InventoryMsResponse> reserveInventory(KafkaEventRequest request) {
		AppUtils.printLog("Request received for inventory");
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
		return spec
				.onStatus(httpStatus -> httpStatus.isSameCodeAs(HttpStatus.NOT_FOUND),
						clientResponse -> handleProductNotFoundInInventory(clientResponse, request, "inventoryMs"))
				.onStatus(httpStatus -> httpStatus.isSameCodeAs(HttpStatus.GONE),
						clientResponse -> handleQuantityNotSufficientInInventory(clientResponse, request, "inventoryMs"))
				.onStatus(httpStatus -> httpStatus.is5xxServerError(),
						clientResponse -> handle5XXError(clientResponse, request, "inventoryMs"))
				.bodyToMono(InventoryMsResponse.class);
	}

	private Mono<OrderMsResponse> updateOrderStatus(String orderId, OrchestratorOrderStatus status,
			String orderFailReason) {
		AppUtils.printLog("Request received for updating order status");
		UpdateOrderStatusRequest updateOrderStatusRequest = new UpdateOrderStatusRequest();
		updateOrderStatusRequest.setStatus(status);
		updateOrderStatusRequest.setOrderFailReason(orderFailReason);

		String requestString;
		try {
			requestString = mapper.writeValueAsString(updateOrderStatusRequest);
		} catch (JsonProcessingException e) {
			throw new InternalServerException(e.getMessage());
		}
		Map<String, String> params = new HashMap<>();
		params.put("orderId", orderId);
		String updateOrderStatusPath = AppUtils
				.replacePathParams(configProperties.getOrder().get("updateOrderStatusPath"), params);
		ResponseSpec spec = orderConfig.put(updateOrderStatusPath, requestString);
		return spec.onStatus(httpStatus -> httpStatus.is4xxClientError(), clientResponse -> {
			return clientResponse.bodyToMono(String.class).flatMap(body -> {
				try {
					OrderMsResponse resp = mapper.readValue(body, OrderMsResponse.class);
					return Mono.error(new ServiceException(resp.getServiceName(), clientResponse.statusCode().value(),
							resp.getResponseMessage()));
				} catch (Exception e) {
					return Mono.error(new ServiceException("order ms", clientResponse.statusCode().value(),
							"Invalid resposne body received from order ms for 4XX errors"));
				}
			});
		}).bodyToMono(OrderMsResponse.class);
	}

	private Mono<ServiceException> handlePaymentUserNotFound(ClientResponse clientResponse, KafkaEventRequest request,
			String serviceName) {
		return clientResponse.bodyToMono(String.class).flatMap(body -> {
			AppUtils.printLog("404 user not found occured from " + serviceName);
			AppUtils.printLog("Body = " + body);
			// TODO: if user not, update order MS status with fail and invoke notification
			// ms service
			return Mono.error(new ServiceException(serviceName, clientResponse.statusCode().value(), body));
		});
	}
	
	private Mono<ServiceException> handleProductNotFoundInInventory(ClientResponse clientResponse, KafkaEventRequest request,
			String serviceName) {
		return clientResponse.bodyToMono(String.class).flatMap(body -> {
			AppUtils.printLog("Error got for, Product not found in the inventory" + serviceName);
			AppUtils.printLog("Body = " + body);
			// TODO: if user not, update order MS status with fail and invoke notification
			// ms service
			return Mono.error(new ServiceException(serviceName, clientResponse.statusCode().value(), body));
		});
	}
	
	private Mono<ServiceException> handleQuantityNotSufficientInInventory(ClientResponse clientResponse, KafkaEventRequest request,
			String serviceName) {
		return clientResponse.bodyToMono(String.class).flatMap(body -> {
			AppUtils.printLog("Error got for, quantity is insufficient in the inventory" + serviceName);
			AppUtils.printLog("Body = " + body);
			// TODO: if user not, update order MS status with fail and invoke notification
			// ms service
			return Mono.error(new ServiceException(serviceName, clientResponse.statusCode().value(), body));
		});
	}

	private Mono<ServiceException> handleAuthorizationError(ClientResponse clientResponse, KafkaEventRequest request,
			String serviceName) {
		return clientResponse.bodyToMono(String.class).flatMap(body -> {
			AppUtils.printLog("Authorization error occured " + serviceName);
			AppUtils.printLog("Body = " + body);
			// TODO: Send order event to DLQ and some persistent store and Raise P1
			return Mono.error(new ServiceException(serviceName, clientResponse.statusCode().value(), body));
		});
	}

	private Mono<ServiceException> handle4XXError(ClientResponse clientResponse, KafkaEventRequest request,
			String serviceName) {
		return clientResponse.bodyToMono(String.class).flatMap(body -> {
			AppUtils.printLog("4XX error occured from " + serviceName);
			AppUtils.printLog("Body = " + body);
			// TODO: We need to think what we will do in such scenario ?
			return Mono.error(new ServiceException(serviceName, clientResponse.statusCode().value(), body));
		});
	}

	private Mono<ServiceException> handle5XXError(ClientResponse clientResponse, KafkaEventRequest request,
			String serviceName) {
		return clientResponse.bodyToMono(String.class).flatMap(body -> {
			AppUtils.printLog("5XX error occured from " + serviceName);
			AppUtils.printLog("Body = " + body);
			// TODO: We need to think what we will do in such scenario ?
			return Mono.error(new ServiceException(serviceName, clientResponse.statusCode().value(), body));
		});

	}

}
