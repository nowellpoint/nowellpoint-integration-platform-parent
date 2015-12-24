package com.nowellpoint.aws.model;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;

public class DynamoDBMapperProvider {
	
	private static DynamoDBMapper mapper;
	
	static {
		String keyId = System.getenv("AWS_KMS_KEY_ID");
		if (keyId == null) {
			keyId = System.getProperty("aws.kms.key.id");
		}
		
		AmazonDynamoDB dynamoDB = new AmazonDynamoDBClient();
		AWSKMS kms = new AWSKMSClient();
		EncryptionMaterialsProvider provider = new DirectKmsMaterialProvider(kms, keyId, null);
		
		mapper = new DynamoDBMapper(dynamoDB, DynamoDBMapperConfig.DEFAULT, new AttributeEncryptor(provider));
	}
	
	private DynamoDBMapperProvider() {
		
	}
	
	public static DynamoDBMapper getDynamoDBMapper() {
		return mapper;
	}
}