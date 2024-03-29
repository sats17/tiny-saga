package com.github.sats17.payment.controller;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sats17.payment.config.Enums.EventName;
import com.github.sats17.payment.config.Enums.OrderStatus;
import com.github.sats17.payment.config.Enums.PaymentFailReason;
import com.github.sats17.payment.config.Enums.PaymentStatus;
import com.github.sats17.payment.config.Enums.TransactionType;
import com.github.sats17.payment.entity.Transaction;
import com.github.sats17.payment.entity.TransactionRepository;
import com.github.sats17.payment.model.KafkaEventRequest;
import com.github.sats17.payment.model.WalletMsResponse;
import com.github.sats17.payment.util.AppUtils;

@Component
public class KafkaController {

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Value("${walletMs.host}")
	private String walletMsHost;

	@Value("${walletMs.basePath}")
	private String walletMsBasePath;

	@Value(value = "${spring.kafka.group_id}")
	private String groupId;

	@Value("${isChoreographyEnabled:false}")
	private boolean kafkaListenerEnabled;

//	@Autowired
//	private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
//
//	@EventListener
//	public void onStarted(ApplicationStartedEvent event) {
//		if (kafkaListenerEnabled) {
//			MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry
//					.getListenerContainer("myListener");
//			listenerContainer.start();
//		}
//	}

	// If want to enable kafka then make autoStartup as true and
	// kafkaListenerEnabled should be true.
	@KafkaListener(topics = { "order-topic" }, autoStartup = "false", groupId = "${spring.kafka.group_id}")
	public void consume(String event) throws InterruptedException {
		System.out.println(groupId);
		KafkaEventRequest eventObj = null;
		try {
			eventObj = mapper.readValue(event, KafkaEventRequest.class);
			AppUtils.printLog("Event recevied = " + eventObj.getEventName());
			switch (eventObj.getEventName()) {
			case ORDER_INITIATED:
				Transaction transaction = buildTransaction(eventObj, "Initiated amount debit process",
						PaymentStatus.PAYMENT_INITIATED, TransactionType.WITHDRAWAL);
				updateTransaction(transaction);
				processOrderInitatedEvent(eventObj);
				break;
			case INVENTORY_INSUFFICIENT:
				processInventoryInsufficientEvent(eventObj);
				break;
			default:
				AppUtils.printLog("Event not supported");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
			System.out.println("Something went wrong in event => " + event);
			System.out.println(e.getMessage());
		}

	}

	private void processOrderInitatedEvent(KafkaEventRequest event) {
		AppUtils.printLog("Processing order intiated event");
		callWalletMSToDebitAmount(event);
	}

	private void processInventoryInsufficientEvent(KafkaEventRequest event) {
		System.out.println("Processing inventory insufficient event");
		callWalletMSToCreditAmount(event);
	}

	// Method used for refund amount
	public void callWalletMSToCreditAmount(KafkaEventRequest event) {
		String baseUrl = walletMsHost + walletMsBasePath + "/credit";

		StringBuilder urlBuilder = new StringBuilder(baseUrl);
		urlBuilder.append("?userId=").append(event.getUserId().trim());
		urlBuilder.append("&amount=").append(event.getPrice());
		AppUtils.printLog("Wallet ms URL = " + urlBuilder.toString());
		URI uri = null;
		try {
			uri = new URI(urlBuilder.toString());
		} catch (URISyntaxException e) {
			AppUtils.printLog("Invalid Wallet MS URL");
			e.printStackTrace();
			return;
		}

		HttpEntity<String> requestEntity = new HttpEntity<>(null);
		try {
			ResponseEntity<WalletMsResponse> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, requestEntity,
					WalletMsResponse.class);

			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				WalletMsResponse response = responseEntity.getBody();
				if (response == null) {
					AppUtils.printLog("Null repsonse from wallet MS, response is not matching as per contract");
					// TODO: Refund fail event
				} else if (response.getStatus() == 20000) {
					AppUtils.printLog("Amount debited successfully for userId " + event.getUserId());
					Transaction transaction = buildTransaction(event, "Refund is succeed", PaymentStatus.REFUND_DONE,
							TransactionType.DEPOSIT);
					updateTransaction(transaction);
					sendRefundDoneEvent(event);
				} else {
					// TODO: Refund fail event
					Transaction transaction = buildTransaction(event, "Refund cannot be proceed",
							PaymentStatus.REFUND_FAILED, TransactionType.DEPOSIT);
					updateTransaction(transaction);
					AppUtils.printLog(
							"Invalid repsonse code from wallet MS, response is not matching as per contract. Repsonse -> "
									+ response.toString());
				}
			} else {
				// TODO: Refund fail event
				Transaction transaction = buildTransaction(event, "Refund cannot be proceed",
						PaymentStatus.REFUND_FAILED, TransactionType.DEPOSIT);
				updateTransaction(transaction);
				AppUtils.printLog("Http status code received from wallet ms is not as per contract, Status code "
						+ responseEntity.getStatusCode());
			}
		} catch (HttpClientErrorException e) {
			// TODO: Refund fail event
			if (e.getStatusCode().equals(HttpStatusCode.valueOf(406))) {
				WalletMsResponse response = e.getResponseBodyAs(WalletMsResponse.class);
				if (response == null) {
					AppUtils.printLog("Invalid repsonse from wallet MS");
					e.printStackTrace();
				} else if (response.getStatus() == 40001) {
					AppUtils.printLog("Insufficient balance in wallet for user id " + event.getUserId().trim());
				} else {
					AppUtils.printLog("Something went wrong from wallet ms. Response -> " + response.toString());
				}
			} else if (e.getStatusCode().equals(HttpStatusCode.valueOf(400))) {
				WalletMsResponse response = e.getResponseBodyAs(WalletMsResponse.class);
				if (response == null) {
					AppUtils.printLog("Invalid repsonse from wallet MS");
					e.printStackTrace();
				} else if (response.getStatus() == 40002) {
					AppUtils.printLog("User Id not present in wallet " + event.getUserId().trim());
				} else {
					AppUtils.printLog("Something went wrong from wallet ms. Response -> " + response.toString());
				}
			}
			Transaction transaction = buildTransaction(event, "Payment cannot be proceed", PaymentStatus.PAYMENT_FAILED,
					TransactionType.WITHDRAWAL);
			updateTransaction(transaction);
		} catch (Exception e) {
			e.printStackTrace();
			Transaction transaction = buildTransaction(event, "Payment cannot be proceed", PaymentStatus.PAYMENT_FAILED,
					TransactionType.WITHDRAWAL);
			updateTransaction(transaction);
		}
	}

	// TODO: Refactor this method
	// Method used for create order
	public void callWalletMSToDebitAmount(KafkaEventRequest event) {
		String baseUrl = walletMsHost + walletMsBasePath + "/debit";

		StringBuilder urlBuilder = new StringBuilder(baseUrl);
		urlBuilder.append("?userId=").append(event.getUserId().trim());
		urlBuilder.append("&amount=").append(event.getPrice());
		AppUtils.printLog("Wallet ms URL = " + urlBuilder.toString());
		URI uri = null;
		try {
			uri = new URI(urlBuilder.toString());
		} catch (URISyntaxException e) {
			AppUtils.printLog("Invalid Wallet MS URL");
			e.printStackTrace();
			return;
		}

		HttpEntity<String> requestEntity = new HttpEntity<>(null);
		try {
			ResponseEntity<WalletMsResponse> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, requestEntity,
					WalletMsResponse.class);

			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				WalletMsResponse response = responseEntity.getBody();
				if (response == null) {
					AppUtils.printLog("Null repsonse from wallet MS, response is not matching as per contract");
					sendPaymentFailEvent(event, PaymentFailReason.PAYMENT_SERVER_ERROR);
				} else if (response.getStatus() == 20000) {
					AppUtils.printLog("Amount debited successfully for userId " + event.getUserId());
					Transaction transaction = buildTransaction(event, "Amount debit is done",
							PaymentStatus.PAYMENT_DONE, TransactionType.WITHDRAWAL);
					updateTransaction(transaction);
					sendPaymentDoneEvent(event);
				} else if (response.getStatus() == 40001) {
					Transaction transaction = buildTransaction(event,
							"Payment cannot be proceed, due to insufficient fund", PaymentStatus.PAYMENT_FAILED,
							TransactionType.WITHDRAWAL);
					updateTransaction(transaction);
					AppUtils.printLog(
							"Payment cannot be proceed, due to insufficient fund. Repsonse -> " + response.toString());
					sendPaymentFailEvent(event, PaymentFailReason.INSUFFICIENT_FUND);
				} else {
					Transaction transaction = buildTransaction(event, "Payment cannot be proceed",
							PaymentStatus.PAYMENT_FAILED, TransactionType.WITHDRAWAL);
					updateTransaction(transaction);
					AppUtils.printLog(
							"Invalid repsonse code from wallet MS, response is not matching as per contract. Repsonse -> "
									+ response.toString());
					sendPaymentFailEvent(event, PaymentFailReason.PAYMENT_SERVER_ERROR);
				}
			} else {
				Transaction transaction = buildTransaction(event, "Payment cannot be proceed",
						PaymentStatus.PAYMENT_FAILED, TransactionType.WITHDRAWAL);
				updateTransaction(transaction);
				AppUtils.printLog("Http status code received from wallet ms is not as per contract, Status code "
						+ responseEntity.getStatusCode());
				sendPaymentFailEvent(event, PaymentFailReason.PAYMENT_SERVER_ERROR);
			}
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode().equals(HttpStatusCode.valueOf(406))) {
				WalletMsResponse response = e.getResponseBodyAs(WalletMsResponse.class);
				if (response == null) {
					AppUtils.printLog("Invalid repsonse from wallet MS");
					e.printStackTrace();
				} else if (response.getStatus() == 40001) {
					Transaction transaction = buildTransaction(event,
							"Payment cannot be proceed, due to insufficient fund", PaymentStatus.PAYMENT_FAILED,
							TransactionType.WITHDRAWAL);
					updateTransaction(transaction);
					AppUtils.printLog(
							"Payment cannot be proceed, due to insufficient fund. Repsonse -> " + response.toString());
					sendPaymentFailEvent(event, PaymentFailReason.INSUFFICIENT_FUND);
				} else {
					Transaction transaction = buildTransaction(event,
							"Payment cannot be proceed, due to payment server error", PaymentStatus.PAYMENT_FAILED,
							TransactionType.WITHDRAWAL);
					updateTransaction(transaction);
					AppUtils.printLog("Something went wrong from wallet ms. Response -> " + response.toString());
					sendPaymentFailEvent(event, PaymentFailReason.PAYMENT_SERVER_ERROR);
				}
			} else if (e.getStatusCode().equals(HttpStatusCode.valueOf(400))) {
				WalletMsResponse response = e.getResponseBodyAs(WalletMsResponse.class);
				if (response == null) {
					Transaction transaction = buildTransaction(event,
							"Payment cannot be proceed, due to server error occured", PaymentStatus.PAYMENT_FAILED,
							TransactionType.WITHDRAWAL);
					updateTransaction(transaction);
					AppUtils.printLog("Something went wrong while doing payment," + event.getUserId().trim());
					sendPaymentFailEvent(event, PaymentFailReason.PAYMENT_SERVER_ERROR);
				} else if (response.getStatus() == 40002) {
					Transaction transaction = buildTransaction(event,
							"Payment cannot be proceed, due to user not found.", PaymentStatus.PAYMENT_FAILED,
							TransactionType.WITHDRAWAL);
					updateTransaction(transaction);
					AppUtils.printLog("User Id not present in wallet " + event.getUserId().trim());
					sendPaymentFailEvent(event, PaymentFailReason.PAYMENT_SERVER_ERROR);
				} else {
					Transaction transaction = buildTransaction(event, "Payment cannot be proceed, server error occured",
							PaymentStatus.PAYMENT_FAILED, TransactionType.WITHDRAWAL);
					updateTransaction(transaction);
					AppUtils.printLog("Something went wrong while doing payment," + event.getUserId().trim());
					sendPaymentFailEvent(event, PaymentFailReason.PAYMENT_SERVER_ERROR);
				}
			} else {
				Transaction transaction = buildTransaction(event, "Payment cannot be proceed",
						PaymentStatus.PAYMENT_FAILED, TransactionType.WITHDRAWAL);
				updateTransaction(transaction);
				AppUtils.printLog("Something went wrong while doing payment," + event.getUserId().trim());
				sendPaymentFailEvent(event, PaymentFailReason.PAYMENT_SERVER_ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Transaction transaction = buildTransaction(event, "Payment cannot be proceed", PaymentStatus.PAYMENT_FAILED,
					TransactionType.WITHDRAWAL);
			updateTransaction(transaction);
			sendPaymentFailEvent(event, PaymentFailReason.PAYMENT_SERVER_ERROR);
		}
	}

	private Transaction buildTransaction(KafkaEventRequest event, String description, PaymentStatus paymentStatus,
			TransactionType transactionType) {
		Transaction transaction = new Transaction();
		transaction.setOrderId(event.getOrderId());
		transaction.setAmount(event.getPrice());
		transaction.setDescription(description);
		transaction.setUserId(event.getUserId());
		transaction.setTimestamp(AppUtils.generateEpochTimestamp());
		transaction.setCurrency("INR");
		transaction.setTransactionId(AppUtils.generateUniqueID());
		transaction.setPaymentStatus(paymentStatus);
		transaction.setTransactionType(transactionType);

		return transaction;
	}

	private void updateTransaction(Transaction transaction) {
		transactionRepository.save(transaction);
	}

	private void publishMessageToTopic(String topicName, String message) {
		kafkaTemplate.send(topicName, message);
	}

	private void sendPaymentDoneEvent(KafkaEventRequest request) {
		KafkaEventRequest pushRequest = new KafkaEventRequest();
		pushRequest.setEventId(AppUtils.generateUniqueID());
		pushRequest.setCorrelationId(request.getCorrelationId());
		pushRequest.setEventName(EventName.PAYMENT_DONE);
		pushRequest.setVersion("1.0");
		pushRequest.setTimestamp(AppUtils.generateEpochTimestamp());
		pushRequest.setOrderId(request.getOrderId());
		pushRequest.setUserId(request.getUserId());
		pushRequest.setOrderStatus(OrderStatus.INITIATED);
		pushRequest.setPaymentStatus(PaymentStatus.PAYMENT_DONE);
		pushRequest.setProductId(request.getProductId());
		pushRequest.setProductQuantity(request.getProductQuantity());
		String data = AppUtils.convertObjectToJsonString(pushRequest);
		publishMessageToTopic("order-topic", data);
	}

	private void sendRefundDoneEvent(KafkaEventRequest request) {
		KafkaEventRequest pushRequest = new KafkaEventRequest();
		pushRequest.setEventId(AppUtils.generateUniqueID());
		pushRequest.setCorrelationId(request.getCorrelationId());
		pushRequest.setEventName(EventName.REFUND_DONE);
		pushRequest.setVersion("1.0");
		pushRequest.setTimestamp(AppUtils.generateEpochTimestamp());
		pushRequest.setOrderId(request.getOrderId());
		pushRequest.setUserId(request.getUserId());
		pushRequest.setOrderStatus(OrderStatus.ORDER_FAIL);
		pushRequest.setPaymentStatus(PaymentStatus.REFUND_FAILED);
		pushRequest.setProductId(request.getProductId());
		pushRequest.setProductQuantity(request.getProductQuantity());
		String data = AppUtils.convertObjectToJsonString(pushRequest);
		publishMessageToTopic("order-topic", data);
	}

	private void sendPaymentFailEvent(KafkaEventRequest request, PaymentFailReason paymentFailReason) {
		KafkaEventRequest pushRequest = new KafkaEventRequest();
		pushRequest.setEventId(AppUtils.generateUniqueID());
		pushRequest.setCorrelationId(request.getCorrelationId());
		pushRequest.setEventName(EventName.PAYMENT_FAIL);
		pushRequest.setVersion("1.0");
		pushRequest.setTimestamp(AppUtils.generateEpochTimestamp());
		pushRequest.setOrderId(request.getOrderId());
		pushRequest.setUserId(request.getUserId());
		pushRequest.setOrderStatus(OrderStatus.INITIATED);
		pushRequest.setPaymentStatus(PaymentStatus.PAYMENT_FAILED);
		pushRequest.setPaymentFailReason(paymentFailReason.toString());
		pushRequest.setProductId(request.getProductId());
		pushRequest.setProductQuantity(request.getProductQuantity());
		String data = AppUtils.convertObjectToJsonString(pushRequest);
		publishMessageToTopic("order-topic", data);
	}
}
