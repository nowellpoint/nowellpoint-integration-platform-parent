package com.nowellpoint.aws.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nowellpoint.aws.model.admin.Property;
import com.nowellpoint.aws.model.admin.PropertyStore;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class CreateConfiguration {
	
	Logger logger = Logger.getLogger(CreateConfiguration.class.getName());

	public CreateConfiguration() {
		
		String accountId = System.getenv("DEFAULT_ACCOUNT_ID");
		
		String[] propertyKeys = new String[] {
				"LOGGLY_API_KEY",
				"MONGO_CLIENT_URI",
				"SALESFORCE_CLIENT_ID",
				"SALESFORCE_CLIENT_SECRET",
				"SALESFORCE_TOKEN_URI",
				"SALESFORCE_REFRESH_URI",
				"SALESFORCE_REVOKE_URI",
				"REDIRECT_URI",
				"STORMPATH_API_KEY_ID",
				"STORMPATH_API_KEY_SECRET",
				"STORMPATH_API_ENDPOINT",
				"STORMPATH_APPLICATION_ID",
				"STORMPATH_DIRECTORY_ID",
				"NOWELLPOINT_API_ENDPOINT",
				"SENDGRID_API_KEY",
				"AWS_X_API_KEY",
				"REDIS_PASSWORD",
				"DEFAULT_ACCOUNT_ID"
		};
		
		DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
		
		List<Property> properties = new ArrayList<Property>();
		
		Arrays.asList(propertyKeys).stream().forEach(key -> {
			
			Property property = new Property();
			property.setStore(PropertyStore.PRODUCTION.name());
			property.setKey(key.replaceAll("_", ".").toLowerCase());
			property.setValue(System.getenv(key));
			property.setLastModifiedBy(accountId);
			
			properties.add(property);
		});
		
		mapper.batchSave(properties);
	}

	public static void main(String[] args) {
		new CreateConfiguration();
	}
}
