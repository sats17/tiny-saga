package com.github.sats17.saga.order.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.sats17.saga.order.configuration.Enums.EventName;
import com.github.sats17.saga.order.configuration.Enums.OrderStatus;
import com.github.sats17.saga.order.configuration.Enums.PaymentStatus;
import com.github.sats17.saga.order.configuration.Enums.PaymentType;
@JsonInclude(Include.NON_NULL)
public class KafkaEventRequest {

	private String eventId;
	private String correlationId;
	private EventName eventName;
	private String version;
	private long timestamp;
	private String orderId;
	private String userId;
	private OrderStatus orderStatus;
	private PaymentStatus paymentStatus;
	private PaymentType paymentType;
	private String productId;
	private int productQuantity;
	private long price;

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
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

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	@Override
	public String toString() {
		return "KafkaEventRequest [eventId=" + eventId + ", correlationId=" + correlationId + ", eventName=" + eventName
				+ ", version=" + version + ", timestamp=" + timestamp + ", orderId=" + orderId + ", userId=" + userId
				+ ", orderStatus=" + orderStatus + ", paymentStatus=" + paymentStatus + ", paymentType=" + paymentType
				+ ", productId=" + productId + ", productQuantity=" + productQuantity + ", price=" + price + "]";
	}

}
