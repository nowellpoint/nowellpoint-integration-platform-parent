package com.nowellpoint.aws.api.model.dynamodb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

public class Callback {
	
	@DynamoDBRangeKey(attributeName="Type")
	private String type;
	
	@DynamoDBAttribute(attributeName="QueryString")  
	private String queryString;
	
	@DynamoDBAttribute(attributeName="Create")
	private Boolean create;
	
	@DynamoDBAttribute(attributeName="Update")
	private Boolean update;
	
	@DynamoDBAttribute(attributeName="Delete")
	private Boolean delete;
	
	public Callback() {
		
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

	public Boolean getCreate() {
		return create;
	}

	public void setCreate(Boolean create) {
		this.create = create;
	}

	public Boolean getUpdate() {
		return update;
	}

	public void setUpdate(Boolean update) {
		this.update = update;
	}

	public Boolean getDelete() {
		return delete;
	}

	public void setDelete(Boolean delete) {
		this.delete = delete;
	}
}