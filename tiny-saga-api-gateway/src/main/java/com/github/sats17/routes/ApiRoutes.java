package com.github.sats17.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiRoutes {

	@Value("${services.order.requestPath}")
	private String orderRequestPath;

	@Value("${services.order.downstreamPath}")
	private String orderDownStreamPath;

	@Value("${services.order.downstreamHost}")
	private String orderDownStreamHost;

	@Bean
	RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("v1_create_order", r -> r.path(orderRequestPath)
						.filters(f -> f.rewritePath(orderRequestPath, orderDownStreamPath)).uri(orderDownStreamHost))
				.build();
	}

}
