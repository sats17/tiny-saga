package com.github.sats17.saga.order.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.sats17.saga.order.enums.Status;
import com.github.sats17.saga.order.model.db.Order;
import com.github.sats17.saga.order.model.db.OrderStatus;
import com.github.sats17.saga.order.repository.OrderRepository;

@Service
public class OrderService {
	
	@Autowired
	OrderRepository orderRepository;
	
	public Order createOrder(Long orderId, String userId, Long productId) throws Exception {
		Order order = new Order();
		order.setOrderId(orderId);
		order.setProductId(productId);
		order.setUserId(userId);
		OrderStatus orderStatus = new OrderStatus();
		orderStatus.setIsInventoryUpdated(false);
		orderStatus.setIsPaymentDone(false);
		orderStatus.setStatus(Status.Initialized);
		order.setOrderStatus(orderStatus);
		Order responseOrder = orderRepository.save(order);
		if(responseOrder.getOrderId() != null) {
			return responseOrder;
		} else {
			throw new Exception("Order creation failed");
		}
		
	}

	public Order getOrder(Long orderId) throws Exception {
		Optional<Order> order = orderRepository.findById(orderId);
		 if(order.isPresent()) {
			 return order.get();
		 } else {
			 throw new Exception("Order not found");
		 }
	}

}
