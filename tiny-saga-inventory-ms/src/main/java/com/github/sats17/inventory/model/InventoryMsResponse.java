package com.github.sats17.inventory.model;

public class InventoryMsResponse {

	private int status;
	private String responseMessage;
	private final String serviceName = "Inventory MS";

	public InventoryMsResponse(int status, String responseMessage) {
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
