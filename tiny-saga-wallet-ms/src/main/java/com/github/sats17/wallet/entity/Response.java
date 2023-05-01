package com.github.sats17.wallet.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

	private int status;
	private String responseMessage;
	private Double amount;

	public Response(int status, String responseMessage) {
		this.setStatus(status);
		this.setResponseMessage(responseMessage);
	}

	public Response(int status, Double amount, String responseMessage) {
		this.setStatus(status);
		this.setResponseMessage(responseMessage);
		this.amount = amount;
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

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

}
