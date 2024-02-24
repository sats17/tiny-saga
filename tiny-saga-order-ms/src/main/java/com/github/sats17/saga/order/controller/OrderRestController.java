package com.github.sats17.saga.order.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
import com.github.sats17.saga.order.repository.OrderRepository;
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
@RequestMapping
public class OrderRestController {

	@Autowired
	public OrderService orderService;

	@Autowired
	public OrderRepository repository;

	@PostMapping("/v1/api/order")
	public ResponseEntity<FinalResponse<Order>> createOrder(@RequestBody CreateOrderSchema orderSchema)
			throws Exception {
		String orderId = OrderUtils.generateOrderId();
		return ApiResponseUtility.successResponseCreator(orderService.createOrder(orderId, orderSchema.getUserId(),
				orderSchema.getProductId(), orderSchema.getPrice(), orderSchema.getProductQuantity(), "order-topic"));
	}

	@GetMapping("/v1/api/order/{orderId}")
	public ResponseEntity<FinalResponse<Order>> getOrder(@PathVariable String orderId) throws Exception {
		return ApiResponseUtility.successResponseCreator(orderService.getOrder(orderId));
	}

	@GetMapping("/v1/api/order/dev/orders")
	public List<Order> getAllProducts() {
		OrderUtils.printLog("Data present in orders DB " + repository.count());
		Iterable<Order> transactionIterable = repository.findAll();
		return StreamSupport.stream(transactionIterable.spliterator(), false).collect(Collectors.toList());
	}

	@PostMapping("/v2/api/order")
	public ResponseEntity<FinalResponse<Order>> createOrderV2(@RequestBody CreateOrderSchema orderSchema)
			throws Exception {
		String orderId = OrderUtils.generateOrderId();
		return ApiResponseUtility.successResponseCreator(
				orderService.createOrder(orderId, orderSchema.getUserId(), orderSchema.getProductId(),
						orderSchema.getPrice(), orderSchema.getProductQuantity(), "orchestrator-topic"));
	}

	@GetMapping("/v2/api/order/{orderId}")
	public ResponseEntity<FinalResponse<Order>> getOrderV2(@PathVariable String orderId) throws Exception {
		return ApiResponseUtility.successResponseCreator(orderService.getOrder(orderId));
	}

}
