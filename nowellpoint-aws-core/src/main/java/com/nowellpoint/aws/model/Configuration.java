package com.nowellpoint.aws.model;

import java.io.IOException;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Configuration {
	
	private static final Logger log = Logger.getLogger(Configuration.class.getName());
	
	static {
		log.info("loading configuration.json");
		long startTime = System.currentTimeMillis();
		try {
			JsonNode node = new ObjectMapper().readValue(Configuration.class.getResourceAsStream("/configuration.json"), JsonNode.class);
			salesforceClientId = node.get("salesforce_client_id").asText();
			salesforceClientSecret = node.get("salesforce_client_secret").asText();
			redirectUri = node.get("redirect_uri").asText();
			logglyApiKey = node.get("loggly_api_key").asText();
			mongoClientUri = node.get("mongo_client_uri").asText();
			stormpathApiKeyId = node.get("stormpath_api_key_id").asText();
			stormpathApiKeySecret = node.get("stormpath_api_key_secret").asText();
			stormpathApplicationId = node.get("stormpath_application_id").asText();
			stormpathDirectory = node.get("stormpath_directory").asText();
			awsKmsKeyId = node.get("aws_kms_key_id").asText();
			salesforceTokenUri = node.get("salesforce_token_uri").asText();
			salesforceRefreshUri = node.get("salesforce_refresh_uri").asText();
			salesforceRevokeUri = node.get("salesforce_revoke_uri").asText();
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
	private static String stormpathDirectory;
	private static String awsKmsKeyId;
	private static String salesforceTokenUri;
	private static String salesforceRefreshUri;
	private static String salesforceRevokeUri;
	
	private Configuration() { }
	
	public static String getSalesforceClientId() { return salesforceClientId; }
	
	public static String getSalesforceClientSecret() { return salesforceClientSecret; }
	
	public static String getRedirectUri() { return redirectUri; }
	
	public static String getLogglyApiKey() { return logglyApiKey; }
	
	public static String getMongoClientUri() { return mongoClientUri; }
	
	public static String getStormpathApiKeyId() { return stormpathApiKeyId; }
	
	public static String getStormpathApiKeySecret() { return stormpathApiKeySecret; }
	
	public static String getStormpathApplicationId() { return stormpathApplicationId; }
	
	public static String getStormpathDirectory() { return stormpathDirectory; }
	
	public static String getAwsKmsKeyId() { return awsKmsKeyId; }
	
	public static String getSalesforceTokenUri() { return salesforceTokenUri; }
	
	public static String getSalesforceRefreshUri() { return salesforceRefreshUri; }
	
	public static String getSalesforceRevokeUri() { return salesforceRevokeUri; }
}