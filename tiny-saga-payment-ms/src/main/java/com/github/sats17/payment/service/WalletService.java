package com.github.sats17.payment.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sats17.payment.model.WalletMsResponse;

@Service
public class WalletService {
	
	@Autowired
	HttpClient httpClient;
	
	@Autowired
	ObjectMapper mapper;

	public WalletMsResponse debitAmount(String url, String userId, Long amount) throws InterruptedException, IOException {
		URI uri = URI.create(url + "/?userId=" + userId + "/amount=" + amount);
		HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
		HttpResponse<String> response;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			System.out.println("HTTP GET Request URL: " + uri);
			System.out.println("HTTP GET Response Code: " + response.statusCode());
			System.out.println("HTTP GET Response Body: " + response.body());
			return mapper.readValue(response.body(), WalletMsResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}

}
