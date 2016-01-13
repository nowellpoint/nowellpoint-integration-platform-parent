package com.nowellpoint.aws.provider;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.nowellpoint.aws.model.admin.Property;

public class Properties {
	
	private static DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	public static final String LOGGLY_API_KEY = "loggly.api.key";
	public static final String MONGO_CLIENT_URI = "mongo.client.uri";
	
	public static String getProperty(String key, String store) {
		return mapper.load(Property.class, key, store).getValue();
	}
}