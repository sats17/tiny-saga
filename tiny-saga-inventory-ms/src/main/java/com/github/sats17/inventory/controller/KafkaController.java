package com.github.sats17.inventory.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sats17.inventory.config.Enums.EventName;
import com.github.sats17.inventory.config.Enums.OrderStatus;
import com.github.sats17.inventory.config.Enums.PaymentStatus;
import com.github.sats17.inventory.entity.Inventory;
import com.github.sats17.inventory.entity.InventoryRepository;
import com.github.sats17.inventory.model.KafkaEventRequest;
import com.github.sats17.inventory.utils.AppUtils;

@Component
public class KafkaController {

	@Autowired
	ObjectMapper mapper;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Value(value = "${spring.kafka.group_id}")
	private String groupId;

	@Autowired
	InventoryRepository inventoryRepository;

	@KafkaListener(topics = { "order-topic" }, autoStartup = "false", groupId = "${spring.kafka.group_id}")
	public void consume(String event) throws InterruptedException {
		KafkaEventRequest eventObj = null;
		try {
			eventObj = mapper.readValue(event, KafkaEventRequest.class);
			AppUtils.printLog("Event recevied = "+eventObj.getEventName());
			switch (eventObj.getEventName()) {
			case ORDER_INITIATED:
				AppUtils.printLog("ORDER_INITIATED Event not supported");
				break;
			case INVENTORY_INSUFFICIENT:
				AppUtils.printLog("INVENTORY_INSUFFICIENT Event not supported");
				break;
			case PAYMENT_DONE:
				boolean status = updateInventory(eventObj);
				if(status) {
					AppUtils.printLog("Inventory reserved for product "+eventObj.getProductId());
					sendInventoryReservedEvent(eventObj);
				} else {
					AppUtils.printLog("Inventory reservation failed, sending event for inventory reservation failed");
					sendInventoryReservedFailEvent(eventObj);
				}
				break;
			default:
				AppUtils.printLog("UNKNOWN Event not supported");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
			System.out.println("Something went wrong in event => " + event);
			System.out.println(e.getMessage());
		}
	}

	public boolean updateInventory(KafkaEventRequest eventObj) {
		String productId = eventObj.getProductId();
		Optional<Inventory> inventory = inventoryRepository.findById(productId);
		if (inventory.isEmpty()) {
			AppUtils.printLog("No product found in inventory, Check with administrator. ProductId: "+productId);
			return false;
		} else {
			int rowsAffected = inventoryRepository.updateProductQuantity(productId, eventObj.getProductQuantity());
			if (rowsAffected <= 0) {
				AppUtils.printLog(
						"Quantity is not sufficient for product "+ inventory.get().getProductId()+". Available quantity is " + inventory.get().getProductQuantity());
				return false;
			}
			AppUtils.printLog("Updated quantity for product with id "+inventory.get().getProductId());
			return true;
		}
	}

	private void publishMessageToTopic(String topicName, String message) {
		kafkaTemplate.send(topicName, message);
	}
	
	private void sendInventoryReservedEvent(KafkaEventRequest request) {
		KafkaEventRequest pushRequest = new KafkaEventRequest();
		pushRequest.setEventId(AppUtils.generateUniqueID());
		pushRequest.setCorrelationId(request.getCorrelationId());
		pushRequest.setEventName(EventName.INVENTORY_RESERVERVED);
		pushRequest.setVersion("1.0");
		pushRequest.setTimestamp(AppUtils.generateEpochTimestamp());
		pushRequest.setOrderId(request.getOrderId());
		pushRequest.setUserId(request.getUserId());
		pushRequest.setOrderStatus(OrderStatus.ORDER_PlACED);
		pushRequest.setPaymentStatus(PaymentStatus.PAYMENT_DONE);
		pushRequest.setProductId(request.getProductId());
		pushRequest.setProductQuantity(request.getProductQuantity());
		System.out.println(pushRequest.toString());
		String data = AppUtils.convertObjectToJsonString(pushRequest);
		publishMessageToTopic("order-topic", data);
	}

	private void sendInventoryReservedFailEvent(KafkaEventRequest request) {
		KafkaEventRequest pushRequest = new KafkaEventRequest();
		pushRequest.setEventId(AppUtils.generateUniqueID());
		pushRequest.setCorrelationId(request.getCorrelationId());
		pushRequest.setEventName(EventName.INVENTORY_INSUFFICIENT);
		pushRequest.setVersion("1.0");
		pushRequest.setTimestamp(AppUtils.generateEpochTimestamp());
		pushRequest.setOrderId(request.getOrderId());
		pushRequest.setUserId(request.getUserId());
		pushRequest.setOrderStatus(OrderStatus.ORDER_PlACED);
		pushRequest.setPaymentStatus(PaymentStatus.PAYMENT_DONE);
		pushRequest.setProductId(request.getProductId());
		pushRequest.setProductQuantity(request.getProductQuantity());
		pushRequest.setInventoryFailReason("INSUFFICIENT INVENTORY");
		String data = AppUtils.convertObjectToJsonString(pushRequest);
		publishMessageToTopic("order-topic", data);
	}

	
}
