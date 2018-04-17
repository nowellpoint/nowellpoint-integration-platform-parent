package com.nowellpoint.www.app.util;

public class EnvironmentVariables {
	
	private static final String MONGO_CLIENT_URI = "MONGO_CLIENT_URI";
	private static final String OKTA_API_KEY = "OKTA_API_KEY";
	private static final String OKTA_ORG_URL = "OKTA_ORG_URL";
	private static final String OKTA_CLIENT_ID = "OKTA_CLIENT_ID";
	private static final String OKTA_CLIENT_SECRET = "OKTA_CLIENT_SECRET";
	private static final String OKTA_AUTHORIZATION_SERVER = "OKTA_AUTHORIZATION_SERVER";
	private static final String LOGGLY_API_ENDPOINT = "LOGGLY_API_ENDPOINT";
	private static final String LOGGLY_API_KEY = "LOGGLY_API_KEY";
	
	public static String getMongoClientUri() {
		return getenv(MONGO_CLIENT_URI);
	}
	
	public static String getOktaApiKey() {
		return getenv(OKTA_API_KEY);
	}
	
	public static String getOktaOrgUrl() {
		return getenv(OKTA_ORG_URL);
	}
	
	public static String getOktaClientId() {
		return getenv(OKTA_CLIENT_ID);
	}
	
	public static String getOktaClientSecret() {
		return getenv(OKTA_CLIENT_SECRET);
	}
	
	public static String getOktaAuthorizationServer() {
		return getenv(OKTA_AUTHORIZATION_SERVER);
	}
	
	public static String getLogglyApiEndpoint() {
		return getenv(LOGGLY_API_ENDPOINT);
	}
	
	public static String getLogglyApiKey() {
		return getenv(LOGGLY_API_KEY);
	}
	
	private static String getenv(String name) {
		return System.getenv(name);
	}
}