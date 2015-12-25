package com.nowellpoint.aws.model.admin;

import java.util.Date;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.DoNotEncrypt;

@DynamoDBTable(tableName="Configurations")
public class Configuration {

	@DynamoDBAutoGeneratedKey
	@DynamoDBHashKey(attributeName="Id")  
	private String id;
	
	@DynamoDBAttribute(attributeName="Payload")
	private String payload;
	
	@DynamoDBAttribute(attributeName="CreatedDate")  
	private Date createdDate;
	
	@DynamoDBAttribute(attributeName="LastModifiedDate")  
	private Date lastModifiedDate;
	
	public Configuration() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	@DoNotEncrypt
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@DoNotEncrypt
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	public Configuration withId(String id) {
		setId(id);
		return this;
	}

	public Configuration withPayload(String payload) {
		setPayload(payload);
		return this;
	}
	
	public Configuration withCreatedDate(Date createdDate) {
		setCreatedDate(createdDate);
		return this;
	}
	
	public Configuration withLastModifiedDate(Date lastModifiedDate) {
		setLastModifiedDate(lastModifiedDate);
		return this;
	}
}