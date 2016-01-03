package com.nowellpoint.aws.provider;

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
		if (keyId == null) {
			keyId = "arn:aws:kms:us-east-1:600862814314:key/534e1894-56e5-413b-97fc-a3d6bbc0c51b";
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