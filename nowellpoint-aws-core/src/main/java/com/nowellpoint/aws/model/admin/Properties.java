package com.nowellpoint.aws.model.admin;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class Properties {
	
	private static DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	public static final String LOGGLY_API_KEY = "loggly.api.key";
	public static final String MONGO_CLIENT_URI = "mongo.client.uri";
	public static final String SALESFORCE_CLIENT_ID = "salesforce.client.id";
	public static final String SALESFORCE_CLIENT_SECRET = "salesforce.client.secret";
	public static final String SALESFORCE_TOKEN_URI = "salesforce.token.uri";
	public static final String SALESFORCE_REFRESH_URI = "salesforce.refresh.uri";
	public static final String SALESFORCE_REVOKE_URI = "salesforce.revoke.uri";
	public static final String REDIRECT_URI = "redirect.uri";
	public static final String STORMPATH_API_KEY_ID = "stormpath.api.key.id";
	public static final String STORMPATH_API_KEY_SECRET = "stormpath.api.key.secret";
	public static final String STORMPATH_API_ENDPOINT = "stormpath.api.endpoint";
	public static final String STORMPATH_APPLICATION_ID = "stormpath.application.id";
	public static final String STORMPATH_DIRECTORY_ID = "stormpath.directory.id";
	public static final String NOWELLPOINT_API_ENDPOINT = "nowellpoint.api.endpoint";
	public static final String SENDGRID_API_KEY = "sendgrid.api.key";
	public static final String AWS_X_API_KEY = "aws.x.api.key";
	public static final String REDIS_PASSWORD = "redis.password";
	public static final String DEFAULT_ACCOUNT_ID = "default.account.id";
	
	public static String getProperty(String store, String key) {
		return mapper.load(Property.class, store, key).getValue();
	}
	
	public static String getProperty(PropertyStore store, String key) {
		return getProperty(store.name(), key);
	}
	
	public static Map<String,String> getProperties(PropertyStore store) {
		Property property = new Property();
		property.setStore(store.name());
		DynamoDBQueryExpression<Property> queryExpression = new DynamoDBQueryExpression<Property>().withHashKeyValues(property);
		List<Property> properties = mapper.query(Property.class, queryExpression);
		return properties.stream().collect(Collectors.toMap(Property::getKey, p -> p.getValue()));
	}
	
	public static void setSystemProperties(PropertyStore store) {
		getProperties(PropertyStore.PRODUCTION).entrySet().forEach(property -> {
        	System.setProperty(property.getKey(), property.getValue());
        });
	}
}