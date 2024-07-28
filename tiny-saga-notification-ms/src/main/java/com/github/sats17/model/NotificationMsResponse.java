package com.github.sats17.model;

public class NotificationMsResponse {

	private int status;
	private String responseMessage;
	private final String serviceName = "Notification MS";

	public NotificationMsResponse(int status, String responseMessage) {
		super();
		this.status = status;
		this.responseMessage = responseMessage;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getServiceName() {
		return serviceName;
	}
	
}
