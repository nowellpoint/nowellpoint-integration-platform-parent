package com.nowellpoint.api.model.dynamodb;

import java.util.UUID;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.nowellpoint.util.Assert;

@DynamoDBTable(tableName="VaultEntries")
public class VaultEntry {

	@DynamoDBHashKey(attributeName="Key")
	private String key;
	
	@DynamoDBAttribute(attributeName="Value")  
	private String value;

	public VaultEntry() {
		
	}

	private VaultEntry(String key, String value) {
		super();
		setKey(key);
		setValue(value);
	}
	
	public static VaultEntry of(String value) {
		Assert.assertNotNullOrEmpty(value, "Missing token for VaultEntry");
		return new VaultEntry(UUID.randomUUID().toString().replaceAll("-",  ""), value);
	}
	
	public static VaultEntry of(String key, String value) {
		Assert.assertNotNullOrEmpty(key, "Missing key for VaultEntry");
		Assert.assertNotNullOrEmpty(value, "Missing value to encrypt for VaultEntry");
		return new VaultEntry(key, value);
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
}