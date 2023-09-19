package com.github.sats17.wallet.utils;

import java.util.UUID;

public class AppUtils {

	public static void printLog(String message) {
		System.out.println("WALLET-MS: " + message);
	}

	public static long generateEpochTimestamp() {
		return System.currentTimeMillis();
	}
	
	public static String generateUniqueID() {
        String uniqueID = System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
        return uniqueID;
    }
}
