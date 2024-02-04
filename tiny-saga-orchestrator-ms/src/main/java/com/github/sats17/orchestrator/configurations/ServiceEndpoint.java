package com.github.sats17.orchestrator.configurations;

import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

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
	
	public Mono<Object> get(String uriPath, MultiValueMap<String, String> headers, MultiValueMap<String, String> queryParams) {
		return this.webClient
				   .get()
				   .uri(uriBuilder -> uriBuilder.path(uriPath).queryParams(queryParams).build())
				   .headers(headersList -> {
					   headersList.addAll(headers);
				   })
				   .exchangeToMono(response -> {
					   return response.bodyToMono(String.class);
				   });
	}
	
	public Mono<String> post(String uriPath, String body) {
		return this.webClient
				   .post()
				   .uri(uriBuilder -> uriBuilder.path(uriPath).build())
				   .bodyValue(body)
				   .exchangeToMono(response -> {
					   return response.bodyToMono(String.class);
				   });
	}
	
}
