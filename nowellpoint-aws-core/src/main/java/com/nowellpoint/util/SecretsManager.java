package com.nowellpoint.util;

import java.io.IOException;
import java.util.HashMap;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("unchecked")
public class SecretsManager {
	
	private static HashMap<String, String> secretMap = new HashMap<>(); 
    
    static {
    	BasicAWSCredentials credentials = new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY"), System.getenv("AWS_SECRET_ACCESS_KEY"));
    	
    	AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard()
    			.withRegion(System.getenv("AWS_REGION"))
    			.withCredentials(new AWSStaticCredentialsProvider(credentials))
    			.build();

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(
        		System.getProperty("secret.name") != null ? System.getProperty("secret.name"): System.getenv("AWS_SECRET_NAME"));

        GetSecretValueResult getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        
        final ObjectMapper objectMapper = new ObjectMapper();

        try {
    		secretMap = objectMapper.readValue(getSecretValueResult.getSecretString(), HashMap.class);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
	
	public static String getMongoClientUri() {
		return getSecret("MONGO_CLIENT_URI");
	}
	
	public static String getOktaOrgUrl() {
		return getSecret("OKTA_ORG_URL");
	}
	
	public static String getOktaClientId() {
		return getSecret("OKTA_CLIENT_ID");
	}
	
	public static String getOktaClientSecret() {
		return getSecret("OKTA_CLIENT_SECRET");
	}
	
	public static String getOktaAuthorizationServer() {
		return getSecret("OKTA_AUTHORIZATION_SERVER");
	}
	
	public static String getOktaDefaultGroupId() {
		return getSecret("OKTA_DEFAULT_GROUP_ID");
	}
	
	public static String getOktaApiKey() {
		return getSecret("OKTA_API_KEY");
	}
	
	public static String getLogglyApiEndpoint() {
		return getSecret("LOGGLY_API_ENDPOINT");
	}
	
	public static String getLogglyApiKey() {
		return getSecret("LOGGLY_API_KEY");
	}
	
	public static String getRedisEncryptionKey() {
		return getSecret("REDIS_ENCRYPTION_KEY");
	}
	
	public static String getRedisHost() {
		return getSecret("REDIS_HOST");
	}
	
	public static String getRedisPort() {
		return getSecret("REDIS_PORT");
	}
	
	public static String getRedisPassword() {
		return getSecret("REDIS_PASSWORD");
	}
	
	public static String getSendGridApiKey() {
		return getSecret("SENDGRID_API_KEY");
	}
	
	public static String getBraintreeEnvironment() {
		return getSecret("BRAINTREE_ENVIRONMENT");
	}
	
	public static String getBraintreeMerchantId() {
		return getSecret("BRAINTREE_MERCHANT_ID");
	}
	
	public static String getBraintreePublicKey() {
		return getSecret("BRAINTREE_PUBLIC_KEY");
	}
	
	public static String getBraintreePrivateKey() {
		return getSecret("BRAINTREE_PRIVATE_KEY");
	}

	public static String getSalesforceClientId() {
		return getSecret("SALESFORCE_CLIENT_ID");
	}

	public static String getSalesforceClientSecret() {
		return getSecret("SALESFORCE_CLIENT_SECRET");
	}
	
	private static String getSecret(String secretName) {
		return secretMap.get(secretName);
	}
}
