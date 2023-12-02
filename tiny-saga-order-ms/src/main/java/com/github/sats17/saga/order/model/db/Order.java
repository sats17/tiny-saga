package com.github.sats17.saga.order.model.db;

import com.github.sats17.saga.order.configuration.Enums;
import com.github.sats17.saga.order.enums.Status;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	private String orderId;

	private String userId;

	private Enums.OrderStatus orderStatus;
	private Enums.PaymentStatus paymentStatus;
	private String orderFailReason;

	private String productId;

	private int quantity;
	private Long price;
	private Long createdAt;
	private Long updateAt;
	private String statusInfo;

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

	public String getStatusInfo() {
		return statusInfo;
	}

	public void setStatusInfo(String statusInfo) {
		this.statusInfo = statusInfo;
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

	public Long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Long createdAt) {
		this.createdAt = createdAt;
	}

	public Long getUpdateAt() {
		return updateAt;
	}

	public void setUpdateAt(Long updateAt) {
		this.updateAt = updateAt;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getOrderFailReason() {
		return orderFailReason;
	}

	public void setOrderFailReason(String orderFailReason) {
		this.orderFailReason = orderFailReason;
	}

}
