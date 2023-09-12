package com.github.sats17.payment.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class WalletMsResponse {

	private int status;
	private String responseMessage;
	private Double amount;

	public WalletMsResponse() {
		super();
	}

	public WalletMsResponse(int status, String responseMessage, Double amount) {
		super();
		this.status = status;
		this.responseMessage = responseMessage;
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
