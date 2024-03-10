package com.github.sats17.orchestrator.configurations;

public class Enums {

	public enum EventName {
		ORDER_INITIATED,
		PAYMENT_FAIL,
		INVENTORY_INSUFFICIENT,
		INVENTORY_RESERVERVED,
		PAYMENT_DONE,
		REFUND_DONE
	}

	public enum OrderStatus {
		INITIATED,
		@Deprecated
		PAYMENT_FAIL,
		@Deprecated
		INVENTORY_INSUFFICIENT,
		@Deprecated
		INVENTORY_RESERVERVED,
		@Deprecated
		PAYMENT_DONE,
		ORDER_PlACED,
		ORDER_DELIEVERED,
		ORDER_FAIL
	}

	public enum PaymentType {
		WALLET
	}
	
	public enum PaymentProcessType {
		PAY, REFUND
	}
	
	public enum TransactionType {
		DEPOSIT, WITHDRAWAL
	}
	
	public enum PaymentStatus {
		PAYMENT_INITIATED,
		PAYMENT_DONE,
		PAYMENT_FAILED,
		REFUND_INITIATED,
		REFUND_DONE,
		REFUND_FAILED
	}
	
	
	public enum OrchestratorOrderStatus {
		PAYMENT_FAIL, INVENTORY_INSUFFICIENT, INVENTORY_RESERVERVED, PAYMENT_DONE, REFUND_DONE, REFUND_FAIL
	}
}
