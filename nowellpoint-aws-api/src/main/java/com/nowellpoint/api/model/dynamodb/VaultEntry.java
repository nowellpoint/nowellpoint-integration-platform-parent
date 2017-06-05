package com.nowellpoint.api.model.dynamodb;

import java.util.UUID;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.nowellpoint.util.Assert;

@DynamoDBTable(tableName="VaultEntries")
public class VaultEntry {

	@DynamoDBHashKey(attributeName="Token")
	private String token;
	
	@DynamoDBAttribute(attributeName="Value")  
	private String value;

	public VaultEntry() {
		
	}

	private VaultEntry(String token, String value) {
		super();
		this.token = token;
		this.value = value;
	}
	
	public static VaultEntry of(String value) {
		Assert.assertNotNullOrEmpty(value, "Missing token for VaultEntry");
		return new VaultEntry(UUID.randomUUID().toString().replaceAll("-",  ""), value);
	}
	
	public static VaultEntry of(String token, String value) {
		Assert.assertNotNullOrEmpty(token, "Missing token for VaultEntry");
		Assert.assertNotNullOrEmpty(value, "Missing value to encryp for VaultEntry");
		return new VaultEntry(token, value);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public VaultEntry withValue(String value) {
		setValue(value);
		return this;
	}
}