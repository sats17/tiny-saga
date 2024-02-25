package com.github.sats17.saga.order.model.response;

import com.github.sats17.saga.order.configuration.Enums;

public class OrderDetails {

	private String orderId;
	private String userId;
	private Enums.OrderStatus orderStatus;
	private Enums.PaymentStatus paymentStatus;
	private String orderFailReason;
	private String productId;
	private int quantity;
	private Long price;
	private String statusInfo;

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

	public Enums.OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Enums.OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Enums.PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(Enums.PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getOrderFailReason() {
		return orderFailReason;
	}

	public void setOrderFailReason(String orderFailReason) {
		this.orderFailReason = orderFailReason;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public String getStatusInfo() {
		return statusInfo;
	}

	public void setStatusInfo(String statusInfo) {
		this.statusInfo = statusInfo;
	}

}
