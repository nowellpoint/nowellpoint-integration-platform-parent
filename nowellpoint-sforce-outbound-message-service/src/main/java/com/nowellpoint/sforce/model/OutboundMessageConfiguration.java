package com.nowellpoint.sforce.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="OutboundMessageConfigurations")
public class OutboundMessageConfiguration {

	@DynamoDBHashKey(attributeName="OrganizationId")  
	private String organizationId;
	
	@DynamoDBRangeKey(attributeName="Type")
	private String type;
	
	@DynamoDBAttribute(attributeName="QueryString")  
	private String queryString;
	
	public OutboundMessageConfiguration() {
		
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
}