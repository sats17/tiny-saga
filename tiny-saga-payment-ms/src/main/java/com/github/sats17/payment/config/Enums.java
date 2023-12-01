package com.github.sats17.payment.config;

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
	
}
