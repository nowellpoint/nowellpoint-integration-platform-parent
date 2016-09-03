package com.nowellpoint.api.model.dynamodb;

import java.util.Date;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.DoNotEncrypt;

@DynamoDBTable(tableName="UserProperties")
public class UserProperty {

	@DynamoDBHashKey(attributeName="Subject")
	private String subject;
	
	@DynamoDBRangeKey(attributeName="Key")  
	private String key;
	
	@DynamoDBAttribute(attributeName="Value")  
	private String value;
	
	@DynamoDBAttribute(attributeName="LastModifiedBy")
	private String lastModifiedBy;
	
	@DynamoDBAttribute(attributeName="LastModifiedDate")
	private Date lastModifiedDate;

	public UserProperty() {
		
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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
	
	public UserProperty withSubject(String subject) {
		setSubject(subject);
		return this;
	}
	
	public UserProperty withKey(String key) {
		setKey(key);
		return this;
	}
	
	public UserProperty withValue(String value) {
		setValue(value);
		return this;
	}
	
	public UserProperty withLastModifiedBy(String lastModifiedBy) {
		setLastModifiedBy(lastModifiedBy);
		return this;
	}
	
	public UserProperty withLastModifiedDate(Date lastModifiedDate) {
		setLastModifiedDate(lastModifiedDate);
		return this;
	}
}