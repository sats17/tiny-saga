package com.github.sats17.saga.order.utils;

public class OrderUtils {
	
	public static Long generateOrderId() {
		long leftLimit = 1000000L;
	    long rightLimit = 9999999L;
	    long generatedLong = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
	    return generatedLong;
	}

}
