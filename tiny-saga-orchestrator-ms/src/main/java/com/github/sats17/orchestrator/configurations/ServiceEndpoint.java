package com.github.sats17.orchestrator.configurations;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import reactor.core.publisher.Mono;

public class ServiceEndpoint {

	private final WebClient webClient;
	
	public ServiceEndpoint(WebClient webClient) {
		this.webClient = webClient;
	}
	
	public Mono<ClientResponse> get(String uriPath) {
		return this.webClient
				   .get()
				   .uri(uriBuilder -> uriBuilder.path(uriPath).build())
				   .exchangeToMono(response -> {
					   return response.bodyToMono(ClientResponse.class);
				   });
	}
	
	public Mono<ResponseEntity<Void>> get(String uriPath, MultiValueMap<String, String> headers, MultiValueMap<String, String> queryParams) {
		return this.webClient
				   .get()
				   .uri(uriBuilder -> uriBuilder.path(uriPath).queryParams(queryParams).build())
				   .headers(headersList -> {
					   headersList.addAll(headers);
				   })
				   .retrieve()
				   .toBodilessEntity();
	}
	
	public ResponseSpec post(String uriPath, String body) {
		return this.webClient
				   .post()
				   .uri(uriBuilder -> uriBuilder.path(uriPath).build())
				   .bodyValue(body)
				   .retrieve();
				   
	}
	
}
