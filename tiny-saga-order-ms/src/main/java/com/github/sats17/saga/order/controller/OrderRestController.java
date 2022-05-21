package com.github.sats17.saga.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.sats17.saga.order.model.db.Order;
import com.github.sats17.saga.order.model.request.CreateOrderSchema;
import com.github.sats17.saga.order.model.response.FinalResponse;
import com.github.sats17.saga.order.service.OrderService;
import com.github.sats17.saga.order.utils.ApiResponseUtility;
import com.github.sats17.saga.order.utils.OrderUtils;

/**
 * This is demo project hence not adding any error validations.
 * 
 * @author sats17
 *
 */
@RestController
@RequestMapping("/api/order")
public class OrderRestController {
	
	@Autowired
	public OrderService orderService;

	@PostMapping()
	public ResponseEntity<FinalResponse<Order>> createOrder( @RequestBody CreateOrderSchema orderSchema) throws Exception {
		Long orderId = OrderUtils.generateOrderId();
		return ApiResponseUtility.successResponseCreator(orderService.createOrder(orderId, orderSchema.getUserId(), orderSchema.getProductId()));
	}
	
	@GetMapping("/{orderId}")
	public ResponseEntity<FinalResponse<Order>> getOrder( @PathVariable Long orderId) throws Exception {
		return ApiResponseUtility.successResponseCreator(orderService.getOrder(orderId));
	}

}
