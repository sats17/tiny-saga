package com.github.sats17.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.sats17.orchestrator.configurations.Enums.OrchestratorOrderStatus;

@JsonInclude(Include.NON_NULL)
public class UpdateOrderStatusRequest {

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

}
