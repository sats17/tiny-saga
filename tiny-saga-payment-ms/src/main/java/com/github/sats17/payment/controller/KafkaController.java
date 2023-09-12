package com.github.sats17.payment.controller;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sats17.payment.model.KafkaEventRequest;
import com.github.sats17.payment.model.WalletMsResponse;

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

	@Autowired
	private RestTemplate restTemplate;

	@Value("${walletMs.host}")
	private String walletMsHost;

	@Value("${walletMs.basePath}")
	private String walletMsBasePath;

	@KafkaListener(topics = { "order-topic" }, groupId = "${spring.kafka.group_id}")
	public void consume(String event) throws InterruptedException {
		KafkaEventRequest eventObj = null;
		try {
			eventObj = mapper.readValue(event, KafkaEventRequest.class);
			System.out.println("Processing event");
			switch (eventObj.getEventName()) {
			case ORDER_INITIATED:
				processOrderInitatedEvent(eventObj);
				break;
			case INVENTORY_INSUFFICIENT:
				processInventoryInsufficientEvent(eventObj);
				break;
			default:
				System.out.println("Invalid event received");
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
		System.out.println("Processing order intiated event");
		performGetRequest(event);
	}

	private void processInventoryInsufficientEvent(KafkaEventRequest event) {
		System.out.println("Processing inventory insufficient event");
	}

	public void performGetRequest(KafkaEventRequest event) {
		String baseUrl = walletMsHost + walletMsBasePath + "/debit";

		StringBuilder urlBuilder = new StringBuilder(baseUrl);
		urlBuilder.append("?userId=").append(event.getUserId().trim());
		urlBuilder.append("&amount=").append(event.getPrice());
		System.out.println("Wallet ms URL = " + urlBuilder.toString());
		URI uri = null;
		try {
			uri = new URI(urlBuilder.toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}

		HttpEntity<String> requestEntity = new HttpEntity<>(null);
		// Check how to handle errors
		try {
			ResponseEntity<WalletMsResponse> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, requestEntity,
					WalletMsResponse.class);

			if (responseEntity.getStatusCode().is2xxSuccessful()) {

				if (responseEntity.getBody().getStatus() == 20000) {
					System.out.println("Amount debited successfully.");
				} else if (responseEntity.getBody().getStatus() == 40001) {
					System.out.println(responseEntity.getBody().getResponseMessage());
				} else {
					System.out.println("Invoking event for payment failure");
				}
			} else {
				System.out.println("Error: " + responseEntity.getStatusCode());
				System.out.println("Invoking event for payment failure");
			}
		} catch (HttpClientErrorException.BadRequest e) {
			// Handle 400 Bad Request response
			System.out.println("Client error");
		} catch (Exception e) {
			// Handle other exceptions
			e.printStackTrace();
		}
	}

}
