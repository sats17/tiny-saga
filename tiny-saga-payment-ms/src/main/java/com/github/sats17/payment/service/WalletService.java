package com.github.sats17.payment.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sats17.payment.exception.WalletException;
import com.github.sats17.payment.model.WalletMsResponse;
import com.github.sats17.payment.util.AppUtils;

@Service
public class WalletService {
	
	@Autowired
	HttpClient httpClient;
	
	@Autowired
	ObjectMapper mapper;

	public WalletMsResponse debitAmount(String url, String userId, Long amount) throws WalletException {
		URI uri = URI.create(url + "/debit?userId=" + userId + "&amount=" + amount);
		HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.noBody()).build();
		AppUtils.printLog("Wallet MS Request URL: " + uri);
		HttpResponse<String> response;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			AppUtils.printLog("Wallet MS debit amount Response Code: " + response.statusCode());
			AppUtils.printLog("Wallet MS debit amount response Body: " + response.body());
			return mapper.readValue(response.body(), WalletMsResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException(e.getMessage());
		}
	}

	public WalletMsResponse creditAmount(String url, String userId, Long amount) throws WalletException {
		URI uri = URI.create(url + "/credit?userId=" + userId + "&amount=" + amount);
		HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.noBody()).build();
		AppUtils.printLog("Wallet MS Request URL: " + uri);
		HttpResponse<String> response;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			AppUtils.printLog("Wallet MS credit amount Response Code: " + response.statusCode());
			AppUtils.printLog("Wallet MS credit amount response Body: " + response.body());
			return mapper.readValue(response.body(), WalletMsResponse.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException(e.getMessage());
		}
	}

}
