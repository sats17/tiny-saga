package com.github.sats17.saga.order.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.sats17.saga.order.configuration.Enums;
import com.github.sats17.saga.order.enums.Status;
import com.github.sats17.saga.order.model.db.Order;
import com.github.sats17.saga.order.model.db.OrderStatus;
import com.github.sats17.saga.order.model.request.KafkaEventRequest;
import com.github.sats17.saga.order.repository.OrderRepository;
import com.github.sats17.saga.order.utils.OrderUtils;

@Service
public class OrderService {

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Value(value = "${spring.kafka.group_id}")
	private String groupId;

//	@Autowired
//	RawKafkaService rawKafkaService;

	ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();

	public Order createOrder(String orderId, String userId, String productId, Long price, int productQuantity,
			String topicName) throws Exception {
		Order order = new Order();
		order.setOrderId(orderId);
		order.setProductId(productId);
		order.setUserId(userId);
		order.setPrice(price);
		order.setQuantity(productQuantity);
		order.setCreatedAt(OrderUtils.generateEpochTimestamp());
		order.setUpdateAt(OrderUtils.generateEpochTimestamp());

		Order responseOrder = orderRepository.save(order);
		if (responseOrder.getOrderId() != null) {
			KafkaEventRequest orderEvent = new KafkaEventRequest();
			orderEvent.setEventId(OrderUtils.generateUniqueID());
			orderEvent.setCorrelationId(OrderUtils.generateUniqueID());
			orderEvent.setEventName(Enums.EventName.ORDER_INITIATED);
			orderEvent.setOrderId(responseOrder.getOrderId());
			orderEvent.setOrderStatus(Enums.OrderStatus.INITIATED);
			orderEvent.setPaymentType(Enums.PaymentType.WALLET);
			orderEvent.setPrice(price);
			orderEvent.setProductId(responseOrder.getProductId());
			orderEvent.setProductQuantity(productQuantity);
			orderEvent.setTimestamp(OrderUtils.generateEpochTimestamp());
			orderEvent.setUserId(responseOrder.getUserId());
			orderEvent.setVersion("1.0");
			publishMessageToTopic(topicName, writer.writeValueAsString(orderEvent));
			return responseOrder;
		} else {
			throw new Exception("Order creation failed");
		}

	}

	private void publishMessageToTopic(String topicName, String message) {
		kafkaTemplate.send(topicName, message);
	}

	public Order getOrder(String orderId) throws Exception {
		Optional<Order> order = orderRepository.findById(orderId);
		if (order.isPresent()) {
			return order.get();
		} else {
			throw new Exception("Order not found");
		}
	}

}
