package com.github.sats17.payment.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PaymentMsResponse {

	private int status;
	private String responseMessage;
	private final String serviceName = "Payment MS";

	public PaymentMsResponse(int status, String responseMessage) {
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
