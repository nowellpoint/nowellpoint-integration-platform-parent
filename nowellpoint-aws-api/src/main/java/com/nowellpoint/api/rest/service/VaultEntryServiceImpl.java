package com.nowellpoint.api.rest.service;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.nowellpoint.api.model.dynamodb.VaultEntry;
import com.nowellpoint.api.service.VaultEntryService;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class VaultEntryServiceImpl implements VaultEntryService {
	
	private static final DynamoDBMapper dynamoDBMapper = DynamoDBMapperProvider.getDynamoDBMapper();

	@Override
	public VaultEntry store(String value) {
		VaultEntry vaultEntry = VaultEntry.of(value);
		dynamoDBMapper.save(vaultEntry);
		return vaultEntry;
	}

	@Override
	public VaultEntry replace(String token, String value) {
		VaultEntry vaultEntry = VaultEntry.of(token, value);
		dynamoDBMapper.save(vaultEntry);
		return vaultEntry;
	}

	@Override
	public VaultEntry retrive(String token) {
		VaultEntry vaultEntry = dynamoDBMapper.load(VaultEntry.class, token);
		return vaultEntry;
	}

	@Override
	public void remove(String token) {
		VaultEntry vaultEntry = VaultEntry.of(token);
		dynamoDBMapper.delete(vaultEntry);
	}

	@Override
	public List<VaultEntry> findByKey(String key) {
		AttributeValue attributeValue = new AttributeValue(key);
		Condition condition = new Condition().withAttributeValueList(attributeValue);
		DynamoDBQueryExpression<VaultEntry> queryExpression = new DynamoDBQueryExpression<VaultEntry>().withRangeKeyCondition("Key", condition);
		return dynamoDBMapper.query(VaultEntry.class, queryExpression);
	}	
}