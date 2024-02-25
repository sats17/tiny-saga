package com.github.sats17.saga.order.utils;

import java.util.UUID;

public class AppUtils {
	
	public static String generateOrderId() {
		long leftLimit = 1000000L;
	    long rightLimit = 9999999L;
	    long generatedLong = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
	    return String.valueOf(generatedLong);
	}
	
	public static String generateUniqueID() {
        String uniqueID = System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
        return uniqueID;
    }
	
	public static long generateEpochTimestamp() {
		return System.currentTimeMillis();
	}
	
	public static void printLog(String message) {
		System.out.println("ORDER-MS: " + message);
	}

}
