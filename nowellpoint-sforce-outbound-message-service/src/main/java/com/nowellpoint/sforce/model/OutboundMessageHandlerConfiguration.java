package com.nowellpoint.sforce.model;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="OutboundMessageHandlerConfigurations")
public class OutboundMessageHandlerConfiguration {

	@DynamoDBHashKey(attributeName="OrganizationId")  
	private String organizationId;
	
	@DynamoDBMarshalling(marshallerClass = QueryMarshaller.class)
	@DynamoDBAttribute(attributeName="Queries")  
	private List<Query> queries;
	
	@DynamoDBAttribute(attributeName="BucketName")  
	private String bucketName;
	
	@DynamoDBAttribute(attributeName="AwsAccessKey")  
	private String awsAccessKey;
	
	@DynamoDBAttribute(attributeName="AwsSecretAccessKey")  
	private String awsSecretAccessKey;
	
	public OutboundMessageHandlerConfiguration() {
		
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public List<Query> getQueries() {
		return queries;
	}

	public void setQueries(List<Query> queries) {
		this.queries = queries;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getAwsAccessKey() {
		return awsAccessKey;
	}

	public void setAwsAccessKey(String awsAccessKey) {
		this.awsAccessKey = awsAccessKey;
	}

	public String getAwsSecretAccessKey() {
		return awsSecretAccessKey;
	}

	public void setAwsSecretAccessKey(String awsSecretAccessKey) {
		this.awsSecretAccessKey = awsSecretAccessKey;
	}
}