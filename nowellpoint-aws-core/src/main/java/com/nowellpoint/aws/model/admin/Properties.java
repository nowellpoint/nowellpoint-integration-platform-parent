package com.nowellpoint.aws.model.admin;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class Properties {
	
	private static DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	public static final String LOGGLY_API_KEY = "loggly.api.key";
	public static final String MONGO_CLIENT_URI = "mongo.client.uri";
	
	public static String getProperty(String store, String key) {
		return mapper.load(Property.class, store, key).getValue();
	}
	
	public static String getProperty(PropertyStore store, String key) {
		return getProperty(store.name(), key);
	}
}