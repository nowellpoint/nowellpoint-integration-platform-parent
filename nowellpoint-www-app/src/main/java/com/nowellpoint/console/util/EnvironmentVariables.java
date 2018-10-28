package com.nowellpoint.console.util;

public class EnvironmentVariables {
	
	private static final String MONGO_CLIENT_URI = "MONGO_CLIENT_URI";
	private static final String OKTA_API_KEY = "OKTA_API_KEY";
	private static final String OKTA_ORG_URL = "OKTA_ORG_URL";
	private static final String OKTA_CLIENT_ID = "OKTA_CLIENT_ID";
	private static final String OKTA_CLIENT_SECRET = "OKTA_CLIENT_SECRET";
	private static final String OKTA_DEFAULT_GROUP_ID = "OKTA_DEFAULT_GROUP_ID";
	private static final String OKTA_AUTHORIZATION_SERVER = "OKTA_AUTHORIZATION_SERVER";
	private static final String LOGGLY_API_ENDPOINT = "LOGGLY_API_ENDPOINT";
	private static final String LOGGLY_API_KEY = "LOGGLY_API_KEY";
	private static final String REDIS_HOST = "REDIS_HOST";
	private static final String REDIS_ENCRYPTION_KEY = "REDIS_ENCRYPTION_KEY";
	private static final String REDIS_PORT = "REDIS_PORT";
	private static final String REDIS_PASSWORD = "REDIS_PASSWORD";
	private static final String SENDGRID_API_KEY = "SENDGRID_API_KEY";
	private static final String BRAINTREE_ENVIRONMENT = "BRAINTREE_ENVIRONMENT";
	private static final String BRAINTREE_MERCHANT_ID = "BRAINTREE_MERCHANT_ID";
	private static final String BRAINTREE_PUBLIC_KEY = "BRAINTREE_PUBLIC_KEY";
	private static final String BRAINTREE_PRIVATE_KEY = "BRAINTREE_PRIVATE_KEY";
	private static final String SALESFORCE_CLIENT_ID = "SALESFORCE_CLIENT_ID";
	private static final String SALESFORCE_TOKEN_URI = "SALESFORCE_TOKEN_URI";
	private static final String SALESFORCE_CLIENT_SECRET = "SALESFORCE_CLIENT_SECRET";
	private static final String SALESFORCE_AUTHORIZE_URI = "SALESFORCE_AUTHORIZE_URI";
	private static final String SALESFORCE_CALLBACK_URI = "SALESFORCE_CALLBACK_URI";
	
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
	
	public static String getOktaDefaultGroupId() {
		return getenv(OKTA_DEFAULT_GROUP_ID);
	}
	
	public static String getLogglyApiEndpoint() {
		return getenv(LOGGLY_API_ENDPOINT);
	}
	
	public static String getLogglyApiKey() {
		return getenv(LOGGLY_API_KEY);
	}
	
	public static String getRedisEncryptionKey() {
		return getenv(REDIS_ENCRYPTION_KEY);
	}
	
	public static String getRedisHost() {
		return getenv(REDIS_HOST);
	}
	
	public static String getRedisPort() {
		return getenv(REDIS_PORT);
	}
	
	public static String getRedisPassword() {
		return getenv(REDIS_PASSWORD);
	}
	
	public static String getSendGridApiKey() {
		return getenv(SENDGRID_API_KEY);
	}
	
	public static String getBraintreeEnvironment() {
		return getenv(BRAINTREE_ENVIRONMENT);
	}
	
	public static String getBraintreeMerchantId() {
		return getenv(BRAINTREE_MERCHANT_ID);
	}
	
	public static String getBraintreePublicKey() {
		return getenv(BRAINTREE_PUBLIC_KEY);
	}
	
	public static String getBraintreePrivateKey() {
		return getenv(BRAINTREE_PRIVATE_KEY);
	}

	public static String getSalesforceClientId() {
		return getenv(SALESFORCE_CLIENT_ID);
	}

	public static String getSalesforceClientSecret() {
		return getenv(SALESFORCE_CLIENT_SECRET);
	}

	public static String getSalesforceTokenUri() {
		return getenv(SALESFORCE_TOKEN_URI);
	}

	public static String getSalesforceAuthorizeUri() {
		return getenv(SALESFORCE_AUTHORIZE_URI);
	}

	public static String getSalesforceCallbackUri() {
		return getenv(SALESFORCE_CALLBACK_URI);
	}
	
	private static String getenv(String name) {
		return System.getenv(name);
	}
}