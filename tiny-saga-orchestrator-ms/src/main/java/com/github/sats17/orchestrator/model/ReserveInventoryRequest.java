package com.github.sats17.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.sats17.orchestrator.configurations.Enums.OrderStatus;
import com.github.sats17.orchestrator.configurations.Enums.PaymentProcessType;
import com.github.sats17.orchestrator.configurations.Enums.PaymentType;

@JsonInclude(Include.NON_NULL)
public class ReserveInventoryRequest {
	private int quantity;

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "ReserveInventoryMsRequest [quantity=" + quantity + "]";
	}

}
