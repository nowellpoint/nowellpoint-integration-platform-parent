package com.nowellpoint.braintree.webhook;

import java.util.Optional;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.AliasListEntry;
import com.amazonaws.services.kms.model.ListAliasesResult;

public class DynamoDBMapperProvider {
	
	private static String KEY_ALIAS = "alias/DATA_ENCRYPTION_KEY";
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