package com.nowellpoint.api.model.dynamodb;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.nowellpoint.api.model.document.SimpleStorageService;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;
import com.nowellpoint.client.sforce.model.Token;

public class UserProperties {

	private static final DynamoDBMapper dynamoDBMapper = DynamoDBMapperProvider.getDynamoDBMapper();
	private static final String PASSWORD = "password";
	private static final String SECURITY_TOKEN = "security.token";
	private static final String AWS_ACCESS_KEY = "aws.access.key";
	private static final String AWS_SECRET_ACCESS_KEY = "aws.secret.access.key";
	private static final String ACCESS_TOKEN = "access.token";
	private static final String REFRESH_TOKEN = "refresh.token";
	
	public static void batchDelete(List<UserProperty> properties) {
		dynamoDBMapper.batchDelete(properties);
	}
	
	public static void saveSalesforceCredentials(String whoId, String key, String password, String securityToken) {
		List<UserProperty> properties = new ArrayList<UserProperty>();
		
		UserProperty accessTokenProperty = new UserProperty()
				.withSubject(key)
				.withKey(PASSWORD)
				.withValue(password)
				.withLastModifiedBy(whoId)
				.withLastModifiedDate(Date.from(Instant.now()));
		
		properties.add(accessTokenProperty);
		
		UserProperty refreshTokenProperty = new UserProperty()
				.withSubject(key)
				.withKey(SECURITY_TOKEN)
				.withValue(securityToken)
				.withLastModifiedBy(whoId)
				.withLastModifiedDate(Date.from(Instant.now()));
		
		properties.add(refreshTokenProperty);
		
		dynamoDBMapper.batchSave(properties);
	}
	
	public static void saveSalesforceTokens(String whoId, String key, Token token) {
		List<UserProperty> properties = new ArrayList<UserProperty>();
		
		UserProperty accessTokenProperty = new UserProperty()
				.withSubject(key)
				.withKey(ACCESS_TOKEN)
				.withValue(token.getAccessToken())
				.withLastModifiedBy(whoId)
				.withLastModifiedDate(Date.from(Instant.now()));
		
		properties.add(accessTokenProperty);
		
		UserProperty refreshTokenProperty = new UserProperty()
				.withSubject(key)
				.withKey(REFRESH_TOKEN)
				.withValue(token.getRefreshToken())
				.withLastModifiedBy(whoId)
				.withLastModifiedDate(Date.from(Instant.now()));
		
		properties.add(refreshTokenProperty);
		
		dynamoDBMapper.batchSave(properties);
	}
	
	public static void saveAwsCredentials(String subject, String key, SimpleStorageService simpleStoreageService) {
		List<UserProperty> properties = new ArrayList<UserProperty>();
		
		UserProperty awsAccessKey = new UserProperty()
				.withSubject(key)
				.withKey(AWS_ACCESS_KEY)
				.withValue(simpleStoreageService.getAwsAccessKey())
				.withLastModifiedBy(subject)
				.withLastModifiedDate(Date.from(Instant.now()));
		
		properties.add(awsAccessKey);
		
		UserProperty awsSecretAccessKey = new UserProperty()
				.withSubject(key)
				.withKey(AWS_SECRET_ACCESS_KEY)
				.withValue(simpleStoreageService.getAwsSecretAccessKey())
				.withLastModifiedBy(subject)
				.withLastModifiedDate(Date.from(Instant.now()));
		
		properties.add(awsSecretAccessKey);
		
		dynamoDBMapper.batchSave(properties);
	}
	
	public static void clear(String subject) {
		List<UserProperty> properties = UserProperties.queryBySubject(subject);
		UserProperties.batchDelete(properties);
	}
	
	public static List<UserProperty> queryBySubject(String subject) {
		UserProperty userProperty = new UserProperty();
		userProperty.setSubject(subject);
		return query(userProperty);
	}
	
	public static List<UserProperty> query(UserProperty userProperty) {
		DynamoDBQueryExpression<UserProperty> queryExpression = new DynamoDBQueryExpression<UserProperty>()
				.withHashKeyValues(userProperty);
		
		return dynamoDBMapper.query(UserProperty.class, queryExpression);
	}
}