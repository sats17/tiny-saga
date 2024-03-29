package com.github.sats17.orchestrator.utils;

import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AppUtils {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static String convertObjectToJsonString(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void printLog(String message) {
		System.out.println("ORCHESTRATOR-MS: " + message);
	}

	public static long generateEpochTimestamp() {
		return System.currentTimeMillis();
	}

	public static String generateUniqueID() {
		String uniqueID = System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
		return uniqueID;
	}

	public static String buildUrl(String protocol, String host, String port, String path) {
		return protocol + "://" + host + ":" + port + path;
	}

	public static String replacePathParams(String path, Map<String, String> params) {
		for (Map.Entry<String, String> entry : params.entrySet()) {
			path = path.replace("{" + entry.getKey() + "}", entry.getValue());
		}
		return path;
	}
}
