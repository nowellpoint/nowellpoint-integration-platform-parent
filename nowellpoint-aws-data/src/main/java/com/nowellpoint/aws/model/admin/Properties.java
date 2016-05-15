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
	public static final String LOGGLY_API_ENDPOINT = "loggly.api.endpoint";
	public static final String MONGO_CLIENT_URI = "mongo.client.uri";
	public static final String SALESFORCE_CLIENT_ID = "salesforce.client.id";
	public static final String SALESFORCE_CLIENT_SECRET = "salesforce.client.secret";
	public static final String SALESFORCE_AUTHORIZE_URI = "salesforce.authorize.uri";
	public static final String SALESFORCE_TOKEN_URI = "salesforce.token.uri";
	public static final String SALESFORCE_REFRESH_URI = "salesforce.refresh.uri";
	public static final String SALESFORCE_REVOKE_URI = "salesforce.revoke.uri";
	public static final String SALESFORCE_USERNAME = "salesforce.username";
	public static final String SALESFORCE_PASSWORD = "salesforce.password";
	public static final String SALESFORCE_SECURITY_TOKEN = "salesforce.security.token";
	public static final String SALESFORCE_REDIRECT_URI = "salesforce.redirect.uri";
	public static final String STORMPATH_API_KEY_ID = "stormpath.api.key.id";
	public static final String STORMPATH_API_KEY_SECRET = "stormpath.api.key.secret";
	public static final String STORMPATH_API_ENDPOINT = "stormpath.api.endpoint";
	public static final String STORMPATH_APPLICATION_ID = "stormpath.application.id";
	public static final String STORMPATH_DIRECTORY_ID = "stormpath.directory.id";
	public static final String STORMPATH_GROUP_ID = "stormpath.group.id";
	public static final String SENDGRID_API_KEY = "sendgrid.api.key";
	public static final String REDIS_PASSWORD = "redis.password";
	public static final String REDIS_HOST = "redis.host";
	public static final String REDIS_PORT = "redis.port";
	public static final String DEFAULT_SUBJECT = "default.subject";
	public static final String CACHE_DATA_ENCRYPTION_KEY = "cache.data.encryption.key";
	
	public static String getProperty(String store, String key) {
		return mapper.load(Property.class, store, key).getValue();
	}
	
	public static String getProperty(PropertyStore store, String key) {
		return getProperty(store.name(), key);
	}
	
	public static Map<String,Property> getProperties(PropertyStore store) {
		return getProperties(store.name());
	}
	
	public static Map<String,Property> getProperties(String store) {
		Property property = new Property();
		property.setStore(store);
		
		DynamoDBQueryExpression<Property> queryExpression = new DynamoDBQueryExpression<Property>()
				.withHashKeyValues(property);
		
		List<Property> properties = mapper.query(Property.class, queryExpression);
		
		return properties.stream().collect(Collectors.toMap(Property::getKey, p -> p));
	}
	
	public static void setSystemProperties(PropertyStore store) {
		setSystemProperties(store.name());
	}
	
	public static void setSystemProperties(String store) {
		getProperties(store).entrySet().forEach(property -> {
        	System.setProperty(property.getValue().getKey(), property.getValue().getValue());
        });
	}
}