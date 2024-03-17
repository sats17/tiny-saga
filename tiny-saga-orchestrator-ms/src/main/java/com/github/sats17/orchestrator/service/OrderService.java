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
		AppUtils.printLog("Order process started..");
		Mono<Object> paymentMono = processPayment(request).flatMap(paymentMsResponse -> {
			AppUtils.printLog(
					"Success Response recieved from payment, will try to update order status asynchronously.");
			updateOrderStatus(request.getOrderId(), request, OrchestratorOrderStatus.PAYMENT_DONE, null).subscribe();
			AppUtils.printLog("Success response recieved from payment, will process inventory");
			return reserveInventory(request).flatMap(inventoryMsResponse -> updateOrderStatus(request.getOrderId(),
					request, OrchestratorOrderStatus.INVENTORY_RESERVERVED, null));
		});

		// Subscribe method invoke payment ms mono as well as all mono/flux chain that
		// present inside flatmap.
		paymentMono.subscribe();
	}

	private Mono<PaymentMsResponse> processPayment(KafkaEventRequest request) {
		AppUtils.printLog("Payment process started..");
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
						clientResponse -> handleUserNotFoundInWalletForPayment(clientResponse, request, "paymentMs"))
				.onStatus(httpStatus -> httpStatus.isSameCodeAs(HttpStatus.BAD_REQUEST),
						clientResponse -> handleInsufficientFundInUserWallet(clientResponse, request, "paymentMs"))
				.onStatus(httpStatus -> httpStatus.is5xxServerError(),
						clientResponse -> handle5XXError(clientResponse, request, "paymentMs"))
				.bodyToMono(PaymentMsResponse.class);
	}

	private Mono<PaymentMsResponse> processRefund(KafkaEventRequest request) {
		AppUtils.printLog("Refund process started..");
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
		ResponseSpec spec = paymentConfig.post(configProperties.getPayment().get("orderRefundPath"), requestString);
		return spec
				.onStatus(httpStatus -> httpStatus.isSameCodeAs(HttpStatus.NOT_FOUND),
						clientResponse -> handleUserNotFoundInWalletForRefund(clientResponse, request, "paymentMs"))
				.onStatus(httpStatus -> httpStatus.is5xxServerError(),
						clientResponse -> handle5XXError(clientResponse, request, "paymentMs"))
				.bodyToMono(PaymentMsResponse.class);
	}

	private Mono<ServiceException> handleUserNotFoundInWalletForPayment(ClientResponse clientResponse,
			KafkaEventRequest request,
			String serviceName) {
		return clientResponse.bodyToMono(String.class).flatMap(body -> {
			AppUtils.printLog("404 user not found occured from " + serviceName);
			AppUtils.printLog("Body = " + body);
			updateOrderStatus(request.getOrderId(), request, OrchestratorOrderStatus.PAYMENT_FAIL,
					"User is not present or blocked in wallet.").subscribe();
			return Mono.error(new ServiceException(serviceName, clientResponse.statusCode().value(), body));
		});
	}

	private Mono<ServiceException> handleUserNotFoundInWalletForRefund(ClientResponse clientResponse,
			KafkaEventRequest request,
			String serviceName) {
		AppUtils.printLog("404 user not found occured from " + serviceName);
		return clientResponse.bodyToMono(String.class).flatMap(body -> {

			AppUtils.printLog("Body = " + body);
			updateOrderStatus(request.getOrderId(), request, OrchestratorOrderStatus.REFUND_FAIL,
					"User is not present or blocked in wallet.").subscribe();
			return Mono.error(new ServiceException(serviceName, clientResponse.statusCode().value(), body));
		});
	}

	private Mono<ServiceException> handleInsufficientFundInUserWallet(ClientResponse clientResponse,
			KafkaEventRequest request,
			String serviceName) {
		AppUtils.printLog("400 received, user do to have insufficient fund in wallet " + serviceName);

		return clientResponse.bodyToMono(String.class).flatMap(body -> {
			AppUtils.printLog("Body = " + body);
			updateOrderStatus(request.getOrderId(), request, OrchestratorOrderStatus.PAYMENT_FAIL,
					"User do not sufficient fund to place order.").subscribe();
			// TODO: Send notification to user.
			return Mono.error(new ServiceException(serviceName, clientResponse.statusCode().value(), body));
		});
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
						clientResponse -> handleQuantityNotSufficientInInventory(clientResponse, request,
								"inventoryMs"))
				.onStatus(httpStatus -> httpStatus.is5xxServerError(),
						clientResponse -> handle5XXError(clientResponse, request, "inventoryMs"))
				.bodyToMono(InventoryMsResponse.class);
	}

	private Mono<OrderMsResponse> updateOrderStatus(String orderId, KafkaEventRequest request,
			OrchestratorOrderStatus status, String orderFailReason) {
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
		return spec
				.onStatus(httpStatus -> httpStatus.is5xxServerError(),
						clientResponse -> handle5XXError(clientResponse, request, "orderMS"))
				.bodyToMono(OrderMsResponse.class);
	}

	private Mono<ServiceException> handleProductNotFoundInInventory(ClientResponse clientResponse,
			KafkaEventRequest request, String serviceName) {
		return clientResponse.bodyToMono(String.class).flatMap(body -> {
			AppUtils.printLog("Error got for, Product not found in the inventory" + serviceName);
			AppUtils.printLog("Body = " + body);
			Mono<OrderMsResponse> updateOrderStatusMono = updateOrderStatus(request.getOrderId(), request,
					OrchestratorOrderStatus.INVENTORY_INSUFFICIENT,
					"Product is not available in inventory.");
			Mono<PaymentMsResponse> processRefundMono = processRefund(request);
			return Mono.zip(updateOrderStatusMono, processRefundMono)
					.flatMap(result -> {
						AppUtils.printLog("Got response from update order status and process refund.");
						if (result.getT2().getStatus() == 200) {
							AppUtils.printLog("Calling order ms to update refund done status.");
							return updateOrderStatus(request.getOrderId(), request, OrchestratorOrderStatus.REFUND_DONE,
									null)
									.then(Mono.error(
											new ServiceException(serviceName, clientResponse.statusCode().value(),
													body)));
						} else {
							AppUtils.printLog("Payment ms refund did not return success response => "+result.getT2().getResponseMessage());
							// TODO: What to do if refund fail ??
							return Mono.error(
									new ServiceException(serviceName, clientResponse.statusCode().value(), body));
						}

					});

		});
	}

	private Mono<ServiceException> handleQuantityNotSufficientInInventory(ClientResponse clientResponse,
			KafkaEventRequest request, String serviceName) {
		AppUtils.printLog("Error got for, quantity is insufficient in the inventory" + serviceName);
		return clientResponse.bodyToMono(String.class).flatMap(body -> {

			AppUtils.printLog("Body = " + body);
			Mono<OrderMsResponse> updateOrderStatusMono = updateOrderStatus(request.getOrderId(), request,
					OrchestratorOrderStatus.INVENTORY_INSUFFICIENT,
					"Insufficient quantity in inventory.");
			Mono<PaymentMsResponse> processRefundMono = processRefund(request);
			return Mono.zip(updateOrderStatusMono, processRefundMono)
					.flatMap(result -> {
						
						AppUtils.printLog("Got response from update order status and process refund.");
						if (result.getT2().getStatus() == 200) {
							// Update order status after success full payment refund.
							AppUtils.printLog("Calling order ms to update refund done status.");
							return updateOrderStatus(request.getOrderId(), request, OrchestratorOrderStatus.REFUND_DONE,
									null)
									.then(Mono.error(
											new ServiceException(serviceName, clientResponse.statusCode().value(),
													body)));
						} else {
							AppUtils.printLog("Payment ms refund did not return success response => "+result.getT2().getResponseMessage());
							// TODO: What to do if refund fail ??
							return Mono.error(
									new ServiceException(serviceName, clientResponse.statusCode().value(), body));
						}

					});
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
