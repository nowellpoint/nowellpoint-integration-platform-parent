package com.nowellpoint.aws.model.admin;

import java.util.Date;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.DoNotEncrypt;

@DynamoDBTable(tableName="Properties")
public class Property {

	@DynamoDBHashKey(attributeName="Store")  
	private String store;
	
	@DynamoDBRangeKey(attributeName="Key")  
	private String key;
	
	@DynamoDBAttribute(attributeName="Value")  
	private String value;
	
	@DynamoDBAttribute(attributeName="LastModifiedBy")
	private String lastModifiedBy;
	
	@DynamoDBAttribute(attributeName="LastModifiedDate")
	private Date lastModifiedDate;
	
	public Property() {
		
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@DoNotEncrypt
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	@DoNotEncrypt
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
}