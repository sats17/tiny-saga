package com.github.sats17.saga.order.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.sats17.saga.order.configuration.Enums;
import com.github.sats17.saga.order.configuration.Enums.OrchestratorOrderStatus;
import com.github.sats17.saga.order.model.db.Order;
import com.github.sats17.saga.order.model.request.KafkaEventRequest;
import com.github.sats17.saga.order.model.response.OrderDetails;
import com.github.sats17.saga.order.repository.OrderRepository;
import com.github.sats17.saga.order.utils.AppUtils;

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

	public OrderDetails createOrder(String orderId, String userId, String productId, Long price, int productQuantity,
			String topicName) throws Exception {
		Order order = new Order();
		order.setOrderId(orderId);
		order.setProductId(productId);
		order.setUserId(userId);
		order.setPrice(price);
		order.setQuantity(productQuantity);
		order.setCreatedAt(AppUtils.generateEpochTimestamp());
		order.setUpdateAt(AppUtils.generateEpochTimestamp());

		Order responseOrder = orderRepository.save(order);
		if (responseOrder.getOrderId() != null) {
			KafkaEventRequest orderEvent = new KafkaEventRequest();
			orderEvent.setEventId(AppUtils.generateUniqueID());
			orderEvent.setCorrelationId(AppUtils.generateUniqueID());
			orderEvent.setEventName(Enums.EventName.ORDER_INITIATED);
			orderEvent.setOrderId(responseOrder.getOrderId());
			orderEvent.setOrderStatus(Enums.OrderStatus.INITIATED);
			orderEvent.setPaymentType(Enums.PaymentType.WALLET);
			orderEvent.setPrice(price);
			orderEvent.setProductId(responseOrder.getProductId());
			orderEvent.setProductQuantity(productQuantity);
			orderEvent.setTimestamp(AppUtils.generateEpochTimestamp());
			orderEvent.setUserId(responseOrder.getUserId());
			orderEvent.setVersion("1.0");
			publishMessageToTopic(topicName, writer.writeValueAsString(orderEvent));
			return transformOrderData(responseOrder);
		} else {
			throw new Exception("Order creation failed");
		}

	}

	private void publishMessageToTopic(String topicName, String message) {
		kafkaTemplate.send(topicName, message);
	}

	public OrderDetails getOrder(String orderId) throws Exception {
		Optional<Order> order = orderRepository.findById(orderId);
		if (order.isPresent()) {
			Order orderData = order.get();
			return transformOrderData(orderData);
		} else {
			throw new Exception("Order not found");
		}
	}

	private OrderDetails transformOrderData(Order order) {
		OrderDetails orderDetails = new OrderDetails();
		orderDetails.setOrderId(order.getOrderId());
		orderDetails.setOrderStatus(order.getOrderStatus());
		orderDetails.setPaymentStatus(order.getPaymentStatus());
		orderDetails.setPrice(order.getPrice());
		orderDetails.setProductId(order.getProductId());
		orderDetails.setQuantity(order.getQuantity());
		orderDetails.setStatusInfo(order.getStatusInfo());
		orderDetails.setUserId(order.getUserId());
		orderDetails.setOrderFailReason(order.getOrderFailReason());
		return orderDetails;
	}

	public void updateOrderStatus(String orderId, OrchestratorOrderStatus status, String orderFailResaon) {
		Optional<Order> order = orderRepository.findById(orderId);
		switch (status) {
		case PAYMENT_DONE:
			if (order.isPresent()) {
				order.get().setPaymentStatus(Enums.PaymentStatus.PAYMENT_DONE);
				order.get().setUpdateAt(AppUtils.generateEpochTimestamp());
				orderRepository.save(order.get());
				AppUtils.printLog("PAYMENT_DONE: Updted order status to payment done");
			} else {
				AppUtils.printLog("Order data not found for orderId: " + orderId);
			}
			break;
		case INVENTORY_RESERVERVED:
			if (order.isPresent()) {
				order.get().setOrderStatus(Enums.OrderStatus.ORDER_PlACED);
				order.get().setUpdateAt(AppUtils.generateEpochTimestamp());
				orderRepository.save(order.get());
				AppUtils.printLog("INVENTORY_RESERVERVED: Updted order status to inventory reserved");
			} else {
				AppUtils.printLog("Order data not found for orderId: " + orderId);
			}
			break;
		case INVENTORY_INSUFFICIENT:
			AppUtils.printLog("INVENTORY_INSUFFICIENT Event not supported");
			if (order.isPresent()) {
				order.get().setPaymentStatus(Enums.PaymentStatus.REFUND_INITIATED);
				order.get().setUpdateAt(AppUtils.generateEpochTimestamp());
				order.get().setOrderStatus(Enums.OrderStatus.ORDER_FAIL);
				order.get().setOrderFailReason(orderFailResaon);
				orderRepository.save(order.get());
				AppUtils.printLog("INVENTORY_INSUFFICIENT: Updted order status to refund initiated and order fail.");
			} else {
				AppUtils.printLog("Order data not found for orderId: " + orderId);
			}
			break;
		case PAYMENT_FAIL:
			if (order.isPresent()) {
				order.get().setPaymentStatus(Enums.PaymentStatus.PAYMENT_FAILED);
				order.get().setOrderStatus(Enums.OrderStatus.ORDER_FAIL);
				order.get().setUpdateAt(AppUtils.generateEpochTimestamp());
				order.get().setOrderFailReason(orderFailResaon);
				orderRepository.save(order.get());
				AppUtils.printLog("PAYMENT_FAIL: Updted order status to payment fail");
			} else {
				AppUtils.printLog("Order data not found for orderId: " + orderId);
			}
			break;
		case REFUND_DONE:
			if (order.isPresent()) {
				order.get().setPaymentStatus(Enums.PaymentStatus.REFUND_DONE);
				order.get().setOrderStatus(Enums.OrderStatus.ORDER_FAIL);
				order.get().setUpdateAt(AppUtils.generateEpochTimestamp());
				orderRepository.save(order.get());
				AppUtils.printLog("PAYMENT_DONE: Updted order status to payment done");
			} else {
				AppUtils.printLog("Order data not found for orderId: " + orderId);
			}
			break;
		}
	}

}
