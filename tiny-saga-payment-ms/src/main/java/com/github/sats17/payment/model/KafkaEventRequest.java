package com.github.sats17.payment.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class KafkaEventRequest {

	private String eventId;
	private String correlationId;
	private EventName eventName;
	private String version;
	private long timestamp;
	private String orderId;
	private Long userId;
	private OrderStatus orderStatus;
	private PaymentType paymentType;
	private String productId;
	private int productQuantity;
	private long price;

	public enum EventName {
		ORDER_INITIATED,
		PAYMENT_FAIL,
		INVENTORY_INSUFFICIENT,
		INVENTORY_RESERVERVED,
		PAYMENT_DONE
	}

	public enum OrderStatus {
		INITIATED,
		PAYMENT_FAIL,
		INVENTORY_INSUFFICIENT,
		INVENTORY_RESERVERVED,
		PAYMENT_DONE
	}

	public enum PaymentType {
		WALLET
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public int getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(int productQuantity) {
		this.productQuantity = productQuantity;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public EventName getEventName() {
		return eventName;
	}

	public void setEventName(EventName eventName) {
		this.eventName = eventName;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

}
