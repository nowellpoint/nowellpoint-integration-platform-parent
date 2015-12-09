package com.nowellpoint.aws.app.data;

import java.time.Instant;
import java.util.Date;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.EncryptionMaterialsProvider;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.model.Event;

public class EventHandler {
	
	private static AWSKMS kms = new AWSKMSClient();
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public static void persist(Object payload) throws JsonProcessingException {
		
		String keyId = System.getenv("AWS_KMS_KEY_ID");
		
		AmazonDynamoDBClient client = new AmazonDynamoDBClient();
		
		EncryptionMaterialsProvider provider = new DirectKmsMaterialProvider(kms, keyId, null);
		DynamoDBMapper mapper = new DynamoDBMapper(client, DynamoDBMapperConfig.DEFAULT, new AttributeEncryptor(provider));
		
		Event event = new Event().withEventDate(Date.from(Instant.now()))
				.withEventStatus(Event.EventStatus.NEW)
				.withEventType(Event.EventType.OUTBOUND_MESSAGE)
				.withOrganizationId("iidiis")
				.withPayload(objectMapper.writeValueAsString(payload))
				.withUserId(null);
		
		mapper.save(event);
	}
}