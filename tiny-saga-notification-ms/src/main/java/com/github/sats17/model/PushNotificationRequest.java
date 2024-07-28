package com.github.sats17.model;

import java.util.List;

import com.github.sats17.configuration.Enums;
import com.github.sats17.configuration.Enums.NotificationType;

public class PushNotificationRequest {

	private String orderId;
	private String productId;
	private String notificationMessage;
	private List<Enums.NotificationType> notificationType;

	public PushNotificationRequest(String orderId, String productId, String notificationMessage,
			List<NotificationType> notificationType) {
		super();
		this.orderId = orderId;
		this.productId = productId;
		this.notificationMessage = notificationMessage;
		this.notificationType = notificationType;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getNotificationMessage() {
		return notificationMessage;
	}

	public void setNotificationMessage(String notificationMessage) {
		this.notificationMessage = notificationMessage;
	}

	public List<Enums.NotificationType> getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(List<Enums.NotificationType> notificationType) {
		this.notificationType = notificationType;
	}

	@Override
	public String toString() {
		return "PushNotificationRequest [orderId=" + orderId + ", productId=" + productId + ", notificationMessage="
				+ notificationMessage + ", notificationType=" + notificationType + "]";
	}

}
