package com.nowellpoint.aws.provider;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.kms.AWSKMSClient;

public class DynamoDBMapperProvider {
	
	private DynamoDBMapper mapper;
	
	public DynamoDBMapperProvider() {
		this(System.getenv("AWS_KMS_KEY_ID"));
	}
	
	public DynamoDBMapperProvider(String kmsKeyId) {
		EncryptionMaterialsProvider provider = new DirectKmsMaterialProvider(new AWSKMSClient(), kmsKeyId, null);			
		mapper = new DynamoDBMapper(new AmazonDynamoDBClient(), DynamoDBMapperConfig.DEFAULT, new AttributeEncryptor(provider));
	}
	
	public DynamoDBMapper getDynamoDBMapper() {
		return mapper;
	}
}