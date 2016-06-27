package com.nowellpoint.aws.api.model.dynamodb;

import java.util.Date;
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
	
	@DynamoDBAttribute(attributeName="ServiceInstanceKey")
	private String serviceInstanceKey;
	
	@DynamoDBAttribute(attributeName="EnvironmentName") 
	private String environmentName;
	
	@DynamoDBAttribute(attributeName="BucketName")  
	private String bucketName;
	
	@DynamoDBAttribute(attributeName="AwsAccessKey")  
	private String awsAccessKey;
	
	@DynamoDBAttribute(attributeName="AwsSecretAccessKey")  
	private String awsSecretAccessKey;
	
	@DynamoDBAttribute(attributeName="DeploymentDate")  
	private Date deploymentDate;
	
	@DynamoDBAttribute(attributeName="DeployedBy")  
	private String deployedBy;
	
	@DynamoDBAttribute(attributeName="IntegrationUser")  
	private String integrationUser;
	
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

	public String getServiceInstanceKey() {
		return serviceInstanceKey;
	}

	public void setServiceInstanceKey(String serviceInstanceKey) {
		this.serviceInstanceKey = serviceInstanceKey;
	}

	public String getEnvironmentName() {
		return environmentName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
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

	public Date getDeploymentDate() {
		return deploymentDate;
	}

	public void setDeploymentDate(Date deploymentDate) {
		this.deploymentDate = deploymentDate;
	}

	public String getDeployedBy() {
		return deployedBy;
	}

	public void setDeployedBy(String deployedBy) {
		this.deployedBy = deployedBy;
	}

	public String getIntegrationUser() {
		return integrationUser;
	}

	public void setIntegrationUser(String integrationUser) {
		this.integrationUser = integrationUser;
	}
}