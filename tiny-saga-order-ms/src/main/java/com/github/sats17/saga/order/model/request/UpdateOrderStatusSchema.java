package com.github.sats17.saga.order.model.request;

import com.github.sats17.saga.order.configuration.Enums.OrchestratorOrderStatus;

public class UpdateOrderStatusSchema {

	private OrchestratorOrderStatus status;
	private String orderFailReason;

	public OrchestratorOrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrchestratorOrderStatus status) {
		this.status = status;
	}

	public String getOrderFailReason() {
		return orderFailReason;
	}

	public void setOrderFailReason(String orderFailReason) {
		this.orderFailReason = orderFailReason;
	}

	public UpdateOrderStatusSchema(OrchestratorOrderStatus status, String orderFailReason) {
		super();
		this.status = status;
		this.orderFailReason = orderFailReason;
	}

}
