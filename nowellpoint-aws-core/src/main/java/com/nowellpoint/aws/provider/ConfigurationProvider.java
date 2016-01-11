package com.nowellpoint.aws.provider;

import java.util.logging.Logger;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.kms.AWSKMSClient;
import com.nowellpoint.aws.model.admin.Configuration;

public class ConfigurationProvider {
	
	private static final Logger log = Logger.getLogger(ConfigurationProvider.class.getName());
	
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
		if (configuration == null) {
			long startTime = System.currentTimeMillis();
			EncryptionMaterialsProvider provider = new DirectKmsMaterialProvider(new AWSKMSClient(), kmsKeyId, null);			
			DynamoDBMapper mapper = new DynamoDBMapper(new AmazonDynamoDBClient(), DynamoDBMapperConfig.DEFAULT, new AttributeEncryptor(provider));
			configuration = mapper.load(Configuration.class, configurationId);
			log.info("configuration file loaded: " + Long.valueOf(System.currentTimeMillis() - startTime));
		}
		return configuration;
	}
}