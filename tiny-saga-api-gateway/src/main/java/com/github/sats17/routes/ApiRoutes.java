package com.github.sats17.routes;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiRoutes {


	@Bean
	RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
					  .route("v1_create_order", r -> r.path("/v1/saga/order")
							  .filters(f -> f.rewritePath("/v1/saga/order/(?<remaining>.*)", "/v1/api/order"))
							  .uri("http:/localhost:8081"))
					  		  .build();
	}
	
}
