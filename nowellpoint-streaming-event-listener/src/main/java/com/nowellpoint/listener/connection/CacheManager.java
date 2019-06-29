package com.nowellpoint.listener.connection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {

	private static final Map<String,String> TOKEN_CACHE = new ConcurrentHashMap<>();
	
	public static void put(String organizationId, String refreshToken) {
		TOKEN_CACHE.put(organizationId, refreshToken);
	}
	
	public static String get(String organizationId) {
		return TOKEN_CACHE.get(organizationId);
	}
	
	public static Boolean contains(String organizationId) {
		return TOKEN_CACHE.containsKey(organizationId);
	}
	
	public static void remove(String organizationId) {
		TOKEN_CACHE.remove(organizationId);
	}
}