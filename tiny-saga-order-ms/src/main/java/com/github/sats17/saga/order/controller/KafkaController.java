package com.github.sats17.saga.order.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sats17.saga.order.configuration.Enums;
import com.github.sats17.saga.order.model.db.Order;
import com.github.sats17.saga.order.model.request.KafkaEventRequest;
import com.github.sats17.saga.order.repository.OrderRepository;
import com.github.sats17.saga.order.utils.OrderUtils;
@Component
public class KafkaController {

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Value(value = "${spring.kafka.group_id}")
	private String groupId;
	
	@Autowired
	private OrderRepository orderRepository;


	@KafkaListener(topics = { "order-topic" }, groupId = "${spring.kafka.group_id}")
	public void consume(String event) throws InterruptedException {
		KafkaEventRequest eventObj = null;
		try {
			eventObj = mapper.readValue(event, KafkaEventRequest.class);
			OrderUtils.printLog("Event recevied = "+eventObj.getEventName());
			switch (eventObj.getEventName()) {
			case ORDER_INITIATED:
				OrderUtils.printLog("ORDER_INITIATED Event not supported");
				break;
			case INVENTORY_INSUFFICIENT:
				OrderUtils.printLog("INVENTORY_INSUFFICIENT Event not supported");
				break;
			case PAYMENT_DONE:
				Optional<Order> order = orderRepository.findById(eventObj.getOrderId());
				if(order.isPresent()) {
					order.get().setPaymentStatus(Enums.PaymentStatus.PAYMENT_DONE);
					order.get().setUpdateAt(OrderUtils.generateEpochTimestamp());
					orderRepository.save(order.get());
					OrderUtils.printLog("PAYMENT_DONE: Updted order status to payment done");
				} else {
					OrderUtils.printLog("Order data not found for orderId: "+eventObj.getOrderId());
				}
				break;
			case INVENTORY_RESERVERVED:
				Optional<Order> orderData = orderRepository.findById(eventObj.getOrderId());
				if(orderData.isPresent()) {
					orderData.get().setOrderStatus(Enums.OrderStatus.ORDER_PlACED);
					orderData.get().setUpdateAt(OrderUtils.generateEpochTimestamp());
					orderRepository.save(orderData.get());
					OrderUtils.printLog("PAYMENT_DONE: Updted order status to payment done");
					OrderUtils.printLog("Sending message to user as order is placed");
				} else {
					OrderUtils.printLog("Order data not found for orderId: "+eventObj.getOrderId());
				}
				break;
			default:
				OrderUtils.printLog("Event not supported");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
			System.out.println("Something went wrong in event => " + event);
			System.out.println(e.getMessage());
		}
	}
}
