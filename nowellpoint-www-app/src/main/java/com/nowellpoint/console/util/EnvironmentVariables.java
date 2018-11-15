package com.nowellpoint.console.util;

public class EnvironmentVariables {
	
	private static final String SALESFORCE_TOKEN_URI = "SALESFORCE_TOKEN_URI";
	private static final String SALESFORCE_AUTHORIZE_URI = "SALESFORCE_AUTHORIZE_URI";
	private static final String SALESFORCE_REDIRECT_URI = "SALESFORCE_REDIRECT_URI";

	public static String getSalesforceTokenUri() {
		return getenv(SALESFORCE_TOKEN_URI);
	}

	public static String getSalesforceAuthorizeUri() {
		return getenv(SALESFORCE_AUTHORIZE_URI);
	}

	public static String getSalesforceRedirectUri() {
		return getenv(SALESFORCE_REDIRECT_URI);
	}
	
	private static String getenv(String name) {
		return System.getenv(name);
	}
}