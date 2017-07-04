package com.nowellpoint.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;

public class DynamoDBMapperProvider {
	
	private static AWSKMS kms = AWSKMSClientBuilder.defaultClient();
	private static DynamoDBMapper mapper;
	
	private DynamoDBMapperProvider() {
		
	}
	
	public static DynamoDBMapper getDynamoDBMapper() {
		return getDynamoDBMapper(getKeyId());
	}
	
	public static DynamoDBMapper getDynamoDBMapper(String kmsKeyId) {
		
		if (mapper == null) {
			EncryptionMaterialsProvider provider = new DirectKmsMaterialProvider(kms, kmsKeyId, null);			
			mapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.defaultClient(), DynamoDBMapperConfig.DEFAULT, new AttributeEncryptor(provider));
		}
		
		return mapper;
	}
	
	private static String getKeyId() {
		return System.getenv("KEY_ID");
	}
}