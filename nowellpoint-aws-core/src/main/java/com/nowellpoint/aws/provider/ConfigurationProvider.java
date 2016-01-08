package com.nowellpoint.aws.provider;

import java.io.IOException;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.model.admin.Configuration;

public class ConfigurationProvider {
	
	private static final Logger log = Logger.getLogger(ConfigurationProvider.class.getName());
	private static Configuration configuration;
	
	static {
		loadConfiguration();
	}
	
	public static void loadConfiguration() {
		
		long startTime = System.currentTimeMillis();
		
		configuration = DynamoDBMapperProvider.getDynamoDBMapper().load(Configuration.class, System.getenv("CONFIGURATION_ID"));
		
		log.info("configuration loaded from database: " + Long.valueOf(System.currentTimeMillis() - startTime));
		
		try {
			JsonNode node = new ObjectMapper().readValue(configuration.getConfigurationFile(), JsonNode.class);
			log.info("configuration parsed into json: " + Long.valueOf(System.currentTimeMillis() - startTime));
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
			redisPassword = node.get("redis_password").asText();
			defaultOrganizationId = node.get("default_organization_id").asText();
			defaultUserId = node.get("default_user_id").asText();
		} catch (IOException e) {
			log.severe("Unable to complete configuration setup: " + e.getMessage());
		}
		
		log.info("configuration file loaded: " + Long.valueOf(System.currentTimeMillis() - startTime));
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
	private static String redisPassword;
	private static String defaultOrganizationId;
	private static String defaultUserId;
	
	private ConfigurationProvider() { }
	
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
	
	public static String getRedisPassword() { return redisPassword; }
	
	public static String getDefaultOrganizationId() { return defaultOrganizationId; }
	
	public static String getDefaultUserId() { return defaultUserId; }
}