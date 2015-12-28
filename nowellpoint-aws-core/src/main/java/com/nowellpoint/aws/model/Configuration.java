package com.nowellpoint.aws.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Configuration {
	
	private static final Logger log = Logger.getLogger(Configuration.class.getName());
	
	static {
		log.info("loading configuration.json");
		long startTime = System.currentTimeMillis();
		try {
			Optional<InputStream> optional = Optional.ofNullable(Configuration.class.getResourceAsStream("/configuration.json"));
			if (optional.isPresent()) {
				JsonNode node = new ObjectMapper().readValue(optional.get(), JsonNode.class);
				salesforceClientId = node.get("salesforce_client_id").asText();
				salesforceClientSecret = node.get("salesforce_client_secret").asText();
				redirectUri = node.get("redirect_uri").asText();
				logglyApiKey = node.get("loggly_api_key").asText();
				mongoClientUri = node.get("mongo_client_uri").asText();
				stormpathApiKeyId = node.get("stormpath_api_key_id").asText();
				stormpathApiKeySecret = node.get("stormpath_api_key_secret").asText();
				stormpathApiEndpoint = node.get("stormpath_api_endpoint").asText();
				stormpathApplicationId = node.get("stormpath_application_id").asText();
				stormpathDirectoryId = node.get("stormpath_directory_id").asText();
				awsKmsKeyId = node.get("aws_kms_key_id").asText();
				salesforceTokenUri = node.get("salesforce_token_uri").asText();
				salesforceRefreshUri = node.get("salesforce_refresh_uri").asText();
				salesforceRevokeUri = node.get("salesforce_revoke_uri").asText();
				salesforceUsername = node.get("salesforce_username").asText();
				salesforcePassword = node.get("salesforce_password").asText();
				salesforceSecurityToken = node.get("salesforce_security_token").asText();
				sendGridApiKey = node.get("sendgrid_api_key").asText();
			} else {
				salesforceClientId = System.getenv("SALESFORCE_CLIENT_ID");
				salesforceClientSecret = System.getenv("SALESFORCE_CLIENT_SECRET");
				redirectUri = System.getenv("REDIRECT_URI");
				logglyApiKey = System.getenv("LOGGLY_API_KEY");
				mongoClientUri = System.getenv("MONGO_CLIENT_URI");
				stormpathApiKeyId = System.getenv("STORMPATH_API_KEY_ID");
				stormpathApiKeySecret = System.getenv("STORMPATH_API_KEY_SECRET");
				stormpathApplicationId = System.getenv("STORMPATH_APPLICATION_ID");
				stormpathDirectoryId = System.getenv("STORMPATH_DIRECTORY_ID");
				stormpathApiEndpoint = System.getenv("STORMPATH_API_ENDPOINT");
				awsKmsKeyId = System.getenv("AWS_KMS_KEY_ID");
				salesforceTokenUri = System.getenv("SALESFORCE_TOKEN_URI");
				salesforceRefreshUri = System.getenv("SALESFORCE_REFRESH_URI");
				salesforceRevokeUri = System.getenv("SALESFORCE_REVOKE_URI");
				salesforceUsername = System.getenv("SALESFORCE_USERNAME");
				salesforcePassword = System.getenv("SALESFORCE_PASSWORD");
				salesforceSecurityToken = System.getenv("SALESFORCE_SECURITY_TOKEN");
				sendGridApiKey = System.getenv("SENDGRID_API_KEY");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("loading configuration.json...complete: " + Long.valueOf(System.currentTimeMillis() - startTime));
	}
	
	private static String salesforceClientId;
	private static String salesforceClientSecret;
	private static String redirectUri;
	private static String logglyApiKey;
	private static String mongoClientUri;
	private static String stormpathApiKeyId;
	private static String stormpathApiKeySecret;
	private static String stormpathApplicationId;
	private static String stormpathDirectoryId;
	private static String stormpathApiEndpoint;
	private static String awsKmsKeyId;
	private static String salesforceTokenUri;
	private static String salesforceRefreshUri;
	private static String salesforceRevokeUri;
	private static String salesforceUsername;
	private static String salesforcePassword;
	private static String salesforceSecurityToken;
	private static String sendGridApiKey;
	
	private Configuration() { }
	
	public static String getSalesforceClientId() { return salesforceClientId; }
	
	public static String getSalesforceClientSecret() { return salesforceClientSecret; }
	
	public static String getRedirectUri() { return redirectUri; }
	
	public static String getLogglyApiKey() { return logglyApiKey; }
	
	public static String getMongoClientUri() { return mongoClientUri; }
	
	public static String getStormpathApiKeyId() { return stormpathApiKeyId; }
	
	public static String getStormpathApiKeySecret() { return stormpathApiKeySecret; }
	
	public static String getStormpathApiEndpoint() { return stormpathApiEndpoint; }
	
	public static String getStormpathApplicationId() { return stormpathApplicationId; }
	
	public static String getStormpathDirectoryId() { return stormpathDirectoryId; }
	
	public static String getAwsKmsKeyId() { return awsKmsKeyId; }
	
	public static String getSalesforceTokenUri() { return salesforceTokenUri; }
	
	public static String getSalesforceRefreshUri() { return salesforceRefreshUri; }
	
	public static String getSalesforceRevokeUri() { return salesforceRevokeUri; }
	
	public static String getSalesforceUsername() { return salesforceUsername; }
	
	public static String getSalesforcePassword() { return salesforcePassword; }
	
	public static String getSalesforceSecurityToken() { return salesforceSecurityToken; }
	
	public static String getSendGridApiKey() { return sendGridApiKey; }
}