package com.github.sats17.saga.order.model.response;

public class BasicOrderMsResponse {

	private int status;
	private String responseMessage;
	private final String serviceName = "Order MS";

	public BasicOrderMsResponse(int status, String responseMessage) {
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
