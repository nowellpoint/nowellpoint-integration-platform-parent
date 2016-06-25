package com.nowellpoint.sforce.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

public class Query {
	
	@DynamoDBRangeKey(attributeName="Type")
	private String type;
	
	@DynamoDBAttribute(attributeName="QueryString")  
	private String queryString;
	
	public Query() {
		
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