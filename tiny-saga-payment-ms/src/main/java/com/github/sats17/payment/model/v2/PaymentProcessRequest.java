package com.github.sats17.payment.model.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.sats17.payment.config.Enums.OrderStatus;
import com.github.sats17.payment.config.Enums.PaymentProcess;
import com.github.sats17.payment.config.Enums.PaymentType;

@JsonInclude(Include.NON_NULL)
public class PaymentProcessRequest {

	private String correlationId;
	private String orderId;
	private String userId;
	private OrderStatus orderStatus;
	private PaymentProcess paymentProcess;
	private PaymentType paymentType;
	private String productId;
	private int productQuantity;
	private long price;
	private String paymentFailReason;

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
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

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public PaymentProcess getPaymentProcess() {
		return paymentProcess;
	}

	public void setPaymentProcess(PaymentProcess paymentProcess) {
		this.paymentProcess = paymentProcess;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
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

	public String getPaymentFailReason() {
		return paymentFailReason;
	}

	public void setPaymentFailReason(String paymentFailReason) {
		this.paymentFailReason = paymentFailReason;
	}

	@Override
	public String toString() {
		return "PaymentProcessRequest [correlationId=" + correlationId + ", orderId=" + orderId + ", userId=" + userId
				+ ", orderStatus=" + orderStatus + ", paymentProcess=" + paymentProcess + ", paymentType=" + paymentType
				+ ", productId=" + productId + ", productQuantity=" + productQuantity + ", price=" + price
				+ ", paymentFailReason=" + paymentFailReason + "]";
	}

}
