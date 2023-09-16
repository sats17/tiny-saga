package com.github.sats17.payment.controller;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
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
import com.github.sats17.payment.util.AppUtils;

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
				if(response == null) {
					AppUtils.printLog("Null repsonse from wallet MS, response is not matching as per contract");
				} else if (response.getStatus() == 20000) {
					AppUtils.printLog("Amount debited successfully for userId "+event.getUserId());
				}  else {
					AppUtils.printLog("Invalid repsonse code from wallet MS, response is not matching as per contract. Repsonse -> "+response.toString());
				}
			} else {
				AppUtils.printLog("Http status code received from wallet ms is not as per contract, Status code "+responseEntity.getStatusCode());
			}
		} catch (HttpClientErrorException e) {
			if(e.getStatusCode().equals(HttpStatusCode.valueOf(406))) {
				WalletMsResponse response = e.getResponseBodyAs(WalletMsResponse.class);
				if(response == null) {
					AppUtils.printLog("Invalid repsonse from wallet MS");
					e.printStackTrace();
				} else if(response.getStatus() == 40001) {
					AppUtils.printLog("Insufficient balance in wallet for user id "+event.getUserId().trim());
				} else {
					AppUtils.printLog("Something went wrong from wallet ms. Response -> "+ response.toString());
				}
			} else if(e.getStatusCode().equals(HttpStatusCode.valueOf(400))) {
				WalletMsResponse response = e.getResponseBodyAs(WalletMsResponse.class);
				if(response == null) {
					AppUtils.printLog("Invalid repsonse from wallet MS");
					e.printStackTrace();
				} else if(response.getStatus() == 40002) {
					AppUtils.printLog("User Id not present in wallet "+event.getUserId().trim());
				} else {
					AppUtils.printLog("Something went wrong from wallet ms. Response -> "+ response.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
