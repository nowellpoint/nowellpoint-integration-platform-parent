package com.nowellpoint.aws.provider;

import java.util.Optional;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.AliasListEntry;
import com.amazonaws.services.kms.model.ListAliasesResult;

public class DynamoDBMapperProvider {
	
	private static String KEY_ALIAS = "alias/DATA_ENCRYPTION_KEY";
	private static AWSKMS kms = new AWSKMSClient();
	private static DynamoDBMapper mapper;
	
	static {
		String kmsKeyId = getKeyId();
		EncryptionMaterialsProvider provider = new DirectKmsMaterialProvider(new AWSKMSClient(), kmsKeyId, null);			
		mapper = new DynamoDBMapper(new AmazonDynamoDBClient(), DynamoDBMapperConfig.DEFAULT, new AttributeEncryptor(provider));
	}
	
	private DynamoDBMapperProvider() {
		
	}
	
	public static DynamoDBMapper getDynamoDBMapper() {
		return mapper;
	}
	
	private static String getKeyId() {

		ListAliasesResult listAliasesResult = kms.listAliases();
		
		Optional<AliasListEntry> aliasListEntry = listAliasesResult.getAliases()
				.stream()
				.filter(entry -> KEY_ALIAS.equals(entry.getAliasName()))
				.findFirst();
		
		String keyId = null;
		
		if (aliasListEntry.isPresent()) {
			keyId = aliasListEntry.get().getTargetKeyId();
		} 

		return keyId;
	}
}