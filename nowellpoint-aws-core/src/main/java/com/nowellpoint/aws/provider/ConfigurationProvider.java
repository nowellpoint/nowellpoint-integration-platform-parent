package com.nowellpoint.aws.provider;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.kms.AWSKMSClient;
import com.nowellpoint.aws.model.admin.Configuration;

public class ConfigurationProvider {
	
	private static Configuration configuration;
	
	private ConfigurationProvider() {

	}
	
	public static Configuration getConfiguration() {
		return getConfiguration(System.getenv("AWS_KMS_KEY_ID"));
	}
	
	public static Configuration getConfiguration(String kmsKeyId) {
		return getConfiguration(kmsKeyId, System.getenv("CONFIGURATION_ID"));
	}
	
	public static Configuration getConfiguration(String kmsKeyId, String configurationId) {
		
		if (kmsKeyId == null || kmsKeyId.trim().isEmpty()) {
			throw new IllegalArgumentException("Missing KMS Key Id parameter. Either set environment variable (AWS_KMS_KEY_ID) or set parameter");
		}
		
		if (configurationId == null || configurationId.trim().isEmpty()) {
			throw new IllegalArgumentException("Missing Configuration Id parameter. Either set environment variable (CONFIGURATION_ID) or set parameter");
		}
		
		if (configuration == null) {
			EncryptionMaterialsProvider provider = new DirectKmsMaterialProvider(new AWSKMSClient(), kmsKeyId, null);			
			DynamoDBMapper mapper = new DynamoDBMapper(new AmazonDynamoDBClient(), DynamoDBMapperConfig.DEFAULT, new AttributeEncryptor(provider));
			configuration = mapper.load(Configuration.class, configurationId);
		}
		
		return configuration;
	}
}