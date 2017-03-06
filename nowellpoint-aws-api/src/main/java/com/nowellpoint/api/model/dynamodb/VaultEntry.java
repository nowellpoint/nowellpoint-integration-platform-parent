package com.nowellpoint.api.model.dynamodb;

import java.util.Date;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.DoNotEncrypt;
import com.nowellpoint.util.Assert;

@DynamoDBTable(tableName="VaultEntries")
public class VaultEntry {

	@DynamoDBHashKey(attributeName="Token")
	private String token;
	
	@DynamoDBRangeKey(attributeName="Key")  
	private String key;
	
	@DynamoDBAttribute(attributeName="Type")  
	private String type;
	
	@DynamoDBAttribute(attributeName="Value")  
	private String value;
	
	@DynamoDBAttribute(attributeName="LastUpdatedOn")
	private Date lastUpdatedOn;
	
	@DynamoDBAttribute(attributeName="LastUpdatedBy")
	private String lastUpdatedBy;

	public VaultEntry() {
		
	}
	
	private VaultEntry(String token) {
		super();
		this.token = token;
	}

	private VaultEntry(String token, String key, String type, String value, Date lastUpdatedOn, String lastUpdatedBy) {
		super();
		this.token = token;
		this.key = key;
		this.type = type;
		this.value = value;
		this.lastUpdatedOn = lastUpdatedOn;
		this.lastUpdatedBy = lastUpdatedBy;
	}
	
	public static VaultEntry of(String token) {
		Assert.assertNotNullOrEmpty(token, "Missing token for VaultEntry");
		return new VaultEntry(token);
	}
	
	public static VaultEntry of(String key, String type, String value, String lastUpdatedBy) {
		Assert.assertNotNullOrEmpty(key, "Missing key for VaultEntry");
		Assert.assertNotNullOrEmpty(type, "Missing type for VaultEntry");
		Assert.assertNotNullOrEmpty(value, "Missing value for VaultEntry");
		Assert.assertNotNullOrEmpty(value, "Missing lastUpdatedBy for VaultEntry");
		return new VaultEntry(UUID.randomUUID().toString().replaceAll("-", ""), key, type, value, new Date(), lastUpdatedBy);
	}
	
	public static VaultEntry of(String token, String key, String type, String value, String lastUpdatedBy) {
		Assert.assertNotNullOrEmpty(token, "Missing token for VaultEntry");
		Assert.assertNotNullOrEmpty(key, "Missing key for VaultEntry");
		Assert.assertNotNullOrEmpty(type, "Missing type for VaultEntry");
		Assert.assertNotNullOrEmpty(value, "Missing value for VaultEntry");
		Assert.assertNotNullOrEmpty(value, "Missing lastUpdatedBy for VaultEntry");
		return new VaultEntry(token, key, type, value, new Date(), lastUpdatedBy);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@DoNotEncrypt
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@DoNotEncrypt
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	@DoNotEncrypt
	public Date getLastUpdatedOn() {
		return lastUpdatedOn;
	}

	public void setLastUpdatedOn(Date lastUpdatedOn) {
		this.lastUpdatedOn = lastUpdatedOn;
	}
	
	public VaultEntry withKey(String key) {
		setKey(key);
		return this;
	}
	
	public VaultEntry withValue(String value) {
		setValue(value);
		return this;
	}
	
	public VaultEntry withLastUpdatedBy(String lastUpdatedBy) {
		setLastUpdatedBy(lastUpdatedBy);
		return this;
	}
}