package com.nowellpoint.aws.model.data;

import java.time.Instant;
import java.util.Date;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;
import com.nowellpoint.aws.model.Event;

public class EventStore {
	
	private static AmazonDynamoDB dynamoDB = new AmazonDynamoDBClient();
	private static AWSKMS kms = new AWSKMSClient();

	public void processEvent(Class<?> type, String organizationId, String userId, String payload) {
		String keyId = System.getenv("AWS_KMS_KEY_ID");
		
		EncryptionMaterialsProvider provider = new DirectKmsMaterialProvider(kms, keyId, null);
		DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB, DynamoDBMapperConfig.DEFAULT, new AttributeEncryptor(provider));
		
		Event event = new Event().withEventDate(Date.from(Instant.now()))
				.withEventStatus(Event.EventStatus.NEW)
				.withType(type.getName())
				.withOrganizationId(organizationId)
				.withUserId(userId)
				.withPayload(payload);
		
		mapper.save(event);
	}
}