package com.github.sats17.payment.util;

import java.util.UUID;

public class AppUtils {

	public static void printLog(String message) {
		System.out.println("PAYMENT-MS: " + message);
	}

	public static long generateEpochTimestamp() {
		return System.currentTimeMillis();
	}
	
	public static String generateUniqueID() {
        String uniqueID = System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
        return uniqueID;
    }
}
