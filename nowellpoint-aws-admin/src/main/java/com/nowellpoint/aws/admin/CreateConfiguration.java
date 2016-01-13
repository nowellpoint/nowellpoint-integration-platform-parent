package com.nowellpoint.aws.admin;

import java.util.Arrays;
import java.util.logging.Logger;

import org.joda.time.Instant;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.model.admin.Configuration;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.admin.Property;
import com.nowellpoint.aws.model.admin.PropertyStore;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class CreateConfiguration {
	
	Logger logger = Logger.getLogger(CreateConfiguration.class.getName());

	public CreateConfiguration() {
		
		String accountId = "https://api.stormpath.com/v1/accounts/5hAh1uolQo18Nk4T8aVxci";
		
		DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
		
		Property logglyApiKey = new Property();
		
		logglyApiKey.setKey("loggly.api.key");
		logglyApiKey.setStore(PropertyStore.LOGGLY.name());
		logglyApiKey.setValue(System.getenv("LOGGLY_API_KEY"));
		logglyApiKey.setLastModifiedBy(accountId);
		
		Property mongoClientUri = new Property();
		
		mongoClientUri.setKey("mongo.client.uri");
		mongoClientUri.setStore(PropertyStore.MONGODB.name());
		mongoClientUri.setValue(System.getenv("MONGO_CLIENT_URI"));
		mongoClientUri.setLastModifiedBy(accountId);
		
		mapper.batchSave(Arrays.asList(logglyApiKey, mongoClientUri));
		
		System.out.println(Properties.getProperty(PropertyStore.LOGGLY, Properties.LOGGLY_API_KEY));
		
//		Configuration configuration = new Configuration();
//		//configuration.setId("7eb82a42-ad99-4077-a149-5894ec26f80d");
//		configuration.setName("production");
//		configuration.setCreatedDate(Instant.now().toDate());
//		configuration.setDefaultAccountId(System.getenv("DEFAULT_ACCOUNT_ID"));
//		configuration.setDefaultOrganizationId(System.getenv("DEFAULT_ORGANIZATION_ID"));
//		configuration.setLastModifiedDate(Instant.now().toDate());
//		configuration.setLogglyApiKey(System.getenv("LOGGLY_API_KEY"));
//		configuration.setMongoClientUri(System.getenv("MONGO_CLIENT_URI"));
//		configuration.setRedirectUri(System.getenv("REDIRECT_URI"));
//		configuration.setRedisPassword(System.getenv("REDIS_PASSWORD"));
//		configuration.setSalesforceClientId(System.getenv("SALESFORCE_CLIENT_ID"));
//		configuration.setSalesforceClientSecret(System.getenv("SALESFORCE_CLIENT_SECRET"));
//		configuration.setSalesforcePassword(System.getenv("SALESFORCE_PASSWORD"));
//		configuration.setSalesforceRefreshUri(System.getenv("SALESFORCE_REFRESH_URI"));
//		configuration.setSalesforceRevokeUri(System.getenv("SALESFORCE_REVOKE_URI"));
//		configuration.setSalesforceSecurityToken(System.getenv("SALESFORCE_SECURITY_TOKEN"));
//		configuration.setSalesforceTokenUri(System.getenv("SALESFORCE_TOKEN_URI"));
//		configuration.setSalesforceUsername(System.getenv("SALESFORCE_USERNAME"));
//		configuration.setSendGridApiKey(System.getenv("SENDGRID_API_KEY"));
//		configuration.setStormpathApiEndpoint(System.getenv("STORMPATH_API_ENDPOINT"));
//		configuration.setStormpathApiKeyId(System.getenv("STORMPATH_API_KEY_ID"));
//		configuration.setStormpathApiKeySecret(System.getenv("STORMPATH_API_KEY_SECRET"));
//		configuration.setStormpathApplicationId(System.getenv("STORMPATH_APPLICATION_ID"));
//		configuration.setStormpathDirectoryId(System.getenv("STORMPATH_DIRECTORY_ID"));
//		
//		ObjectMapper objectMapper = new ObjectMapper();
//		try {
//			logger.info(objectMapper.writeValueAsString(property));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
//				
//		/**
//		 * 
//		 */
//		
//		mapper.save(configuration);
		
//		/**
//		 * 
//		 */
		
//		logger.info("Configuration Id: " + configuration.getId());
	}

	public static void main(String[] args) {
		new CreateConfiguration();
	}
}
