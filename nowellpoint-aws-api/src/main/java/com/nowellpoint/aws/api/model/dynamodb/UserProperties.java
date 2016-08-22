package com.nowellpoint.aws.api.model.dynamodb;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class UserProperties {

	private static final DynamoDBMapper dynamoDBMapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	public static void batchSave(List<UserProperty> properties) {
		dynamoDBMapper.batchSave(properties);
	}
	
	public static void batchDelete(List<UserProperty> properties) {
		dynamoDBMapper.batchDelete(properties);
	}
	
	public static List<UserProperty> query(UserProperty userProperty) {
		DynamoDBQueryExpression<UserProperty> queryExpression = new DynamoDBQueryExpression<UserProperty>()
				.withHashKeyValues(userProperty);
		
		return dynamoDBMapper.query(UserProperty.class, queryExpression);
	}
}