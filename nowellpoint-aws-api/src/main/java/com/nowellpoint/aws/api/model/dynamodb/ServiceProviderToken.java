package com.nowellpoint.aws.api.model.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="ServiceProviderTokens")
public class ServiceProviderToken {

	@DynamoDBHashKey(attributeName="Subject")
	private String subject;
	
	@DynamoDBAttribute(attributeName="Token")
	private String token;
	
	public ServiceProviderToken() {
		
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}