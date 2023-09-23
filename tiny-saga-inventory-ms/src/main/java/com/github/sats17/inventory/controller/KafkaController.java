package com.github.sats17.inventory.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
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

	@KafkaListener(topics = { "order-topic" }, groupId = "${spring.kafka.group_id}")
	public void consume(String event) throws InterruptedException {
		KafkaEventRequest eventObj = null;
		try {
			eventObj = mapper.readValue(event, KafkaEventRequest.class);
			switch (eventObj.getEventName()) {
			case ORDER_INITIATED:
				AppUtils.printLog("Unknown event recevied");
				break;
			case INVENTORY_INSUFFICIENT:
				AppUtils.printLog("Unknown event recevied");
				break;
			case PAYMENT_DONE:
				isInventoryAvailable(eventObj);
			default:
				AppUtils.printLog("Unknown event recevied");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
			System.out.println("Something went wrong in event => " + event);
			System.out.println(e.getMessage());
		}
	}

	public boolean isInventoryAvailable(KafkaEventRequest eventObj) {
		String productId = eventObj.getProductId();
		System.out.println("Quantity "+eventObj.getProductQuantity());
		System.out.println("Id "+eventObj.getProductId());
		Optional<Inventory> inventory = inventoryRepository.findById(productId);
		if (inventory.isEmpty()) {
			AppUtils.printLog("No product found in inventory, Check with administrator");
			return false;
		} else {
			int rowsAffected = inventoryRepository.updateProductQuantity(productId, eventObj.getProductQuantity());
			if (rowsAffected == 0) {
				AppUtils.printLog(
						"Quantity is not sufficient. Available quantity is " + inventory.get().getProductQuantity());
				return false;
			}
			System.out.println("Rows affected "+rowsAffected);
			return true;
		}
	}

//	private void sendPaymentDoneEvent(KafkaEventRequest request) {
//		KafkaEventRequest pushRequest = new KafkaEventRequest();
//		pushRequest.setEventId(AppUtils.generateUniqueID());
//		pushRequest.setCorrelationId(request.getCorrelationId());
//		pushRequest.setEventName(EventName.PAYMENT_DONE);
//		pushRequest.setVersion("1.0");
//		pushRequest.setTimestamp(AppUtils.generateEpochTimestamp());
//		pushRequest.setOrderId(request.getOrderId());
//		pushRequest.setUserId(request.getUserId());
//		pushRequest.setOrderStatus(OrderStatus.INITIATED);
//		pushRequest.setPaymentStatus(PaymentStatus.PAYMENT_DONE);
//		pushRequest.setProductId(request.getProductId());
//		pushRequest.setProductQuantity(request.getProductQuantity());
//		System.out.println(pushRequest.toString());
//		String data = AppUtils.convertObjectToJsonString(pushRequest);
//		publishMessageToTopic("order-topic", data);
//	}
}
