package com.nowellpoint.util;

public class EnvironmentVariables {
	
	private static final String MONGO_CLIENT_URI = "MONGO_CLIENT_URI";
	public static final String SENDGRID_API_KEY = "SENDGRID_API_KEY";
	public static final String REDIS_ENCRYPTION_KEY = "REDIS_ENCRYPTION_KEY";
	public static final String REDIS_HOST = "REDIS_HOST";
	public static final String REDIS_PORT = "REDIS_PORT";
	public static final String REDIS_PASSWORD = "REDIS_PASSWORD";
	
	public static String getMongoClientUri() {
		return getenv(MONGO_CLIENT_URI);
	}
	
	private static String getenv(String name) {
		return System.getenv(name);
	}
}