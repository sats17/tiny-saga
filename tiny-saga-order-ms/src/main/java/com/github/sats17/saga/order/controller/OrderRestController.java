package com.github.sats17.saga.order.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.sats17.saga.order.model.db.Order;
import com.github.sats17.saga.order.model.request.CreateOrderSchema;
import com.github.sats17.saga.order.model.request.UpdateOrderStatusSchema;
import com.github.sats17.saga.order.model.response.BasicOrderMsResponse;
import com.github.sats17.saga.order.model.response.OrderDetails;
import com.github.sats17.saga.order.repository.OrderRepository;
import com.github.sats17.saga.order.service.OrderService;
import com.github.sats17.saga.order.utils.AppUtils;

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
	public ResponseEntity<OrderDetails> createOrder(@RequestBody CreateOrderSchema orderSchema)
			throws Exception {
		String orderId = AppUtils.generateOrderId();
		OrderDetails details = orderService.createOrder(orderId, orderSchema.getUserId(), orderSchema.getProductId(),
				orderSchema.getPrice(), orderSchema.getProductQuantity(), "order-topic");
		return ResponseEntity.status(HttpStatus.OK).body(details);
	}

	@GetMapping("/v1/api/order/{orderId}")
	public ResponseEntity<OrderDetails> getOrder(@PathVariable String orderId) throws Exception {
		OrderDetails details = orderService.getOrder(orderId);
		return ResponseEntity.status(HttpStatus.OK).body(details);
	}

	@GetMapping("/v1/api/order/dev/orders")
	public List<Order> getAllProducts() {
		AppUtils.printLog("Data present in orders DB " + repository.count());
		Iterable<Order> transactionIterable = repository.findAll();
		return StreamSupport.stream(transactionIterable.spliterator(), false).collect(Collectors.toList());
	}

	@GetMapping("/v1/api/order//dev/healthcheck")
	public ResponseEntity<BasicOrderMsResponse> getHealthCheckV1() {
		// Check kafka for order-topic
		AppUtils.printLog("Data present in transaction DB " + repository.count());
		BasicOrderMsResponse response = new BasicOrderMsResponse(200, "Order server and Order DB is up and running");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/v2/api/order")
	public ResponseEntity<OrderDetails> createOrderV2(@RequestBody CreateOrderSchema orderSchema)
			throws Exception {
		String orderId = AppUtils.generateOrderId();
		OrderDetails details = orderService.createOrder(orderId, orderSchema.getUserId(), orderSchema.getProductId(),
				orderSchema.getPrice(), orderSchema.getProductQuantity(), "orchestrator-topic");
		return ResponseEntity.status(HttpStatus.OK).body(details);
	}

	@GetMapping("/v2/api/order/{orderId}")
	public ResponseEntity<OrderDetails> getOrderV2(@PathVariable String orderId) throws Exception {
		OrderDetails details = orderService.getOrder(orderId);
		return ResponseEntity.status(HttpStatus.OK).body(details);
	}

	@PutMapping("/v2/api/order/{orderId}/status")
	public ResponseEntity<BasicOrderMsResponse> updateOrderStatus(@PathVariable String orderId,
			@RequestBody UpdateOrderStatusSchema data) throws Exception {
		AppUtils.printLog("Update order status invoked for "+data.getStatus().toString());
		orderService.updateOrderStatus(orderId, data.getStatus(), data.getOrderFailReason());
		BasicOrderMsResponse response = new BasicOrderMsResponse(200, "Order updated succesfully");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/v2/api/order//dev/healthcheck")
	public ResponseEntity<BasicOrderMsResponse> getHealthCheckV2() {
		// Check kafka for orchestrator-topic
		AppUtils.printLog("Data present in transaction DB " + repository.count());
		BasicOrderMsResponse response = new BasicOrderMsResponse(200, "Order server and Order DB is up and running");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
