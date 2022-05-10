package com.github.sats17.saga.order.model.db;

import com.github.sats17.saga.order.enums.Status;

public class OrderStatus {

	private Status status;

	private Boolean isPaymentDone;

	private Boolean isInventoryUpdated;

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Boolean getIsPaymentDone() {
		return isPaymentDone;
	}

	public void setIsPaymentDone(Boolean isPaymentDone) {
		this.isPaymentDone = isPaymentDone;
	}

	public Boolean getIsInventoryUpdated() {
		return isInventoryUpdated;
	}

	public void setIsInventoryUpdated(Boolean isInventoryUpdated) {
		this.isInventoryUpdated = isInventoryUpdated;
	}

	@Override
	public String toString() {
		return "OrderStatus [status=" + status + ", isPaymentDone=" + isPaymentDone + ", isInventoryUpdated="
				+ isInventoryUpdated + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((isInventoryUpdated == null) ? 0 : isInventoryUpdated.hashCode());
		result = prime * result + ((isPaymentDone == null) ? 0 : isPaymentDone.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderStatus other = (OrderStatus) obj;
		if (isInventoryUpdated == null) {
			if (other.isInventoryUpdated != null)
				return false;
		} else if (!isInventoryUpdated.equals(other.isInventoryUpdated))
			return false;
		if (isPaymentDone == null) {
			if (other.isPaymentDone != null)
				return false;
		} else if (!isPaymentDone.equals(other.isPaymentDone))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

}
