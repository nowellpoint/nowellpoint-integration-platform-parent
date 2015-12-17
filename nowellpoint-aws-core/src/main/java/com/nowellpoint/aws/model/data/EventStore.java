package com.nowellpoint.aws.model.data;

import java.time.Instant;
import java.util.Date;

import org.bson.types.ObjectId;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
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
import com.nowellpoint.aws.model.AbstractPayload;
import com.nowellpoint.aws.model.Event;

public class EventStore {
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	private static AmazonDynamoDB dynamoDB = new AmazonDynamoDBClient();
	private static AWSKMS kms = new AWSKMSClient();

	public void save(AbstractPayload payload) throws JsonProcessingException {
		String keyId = System.getenv("AWS_KMS_KEY_ID");
		
		if (payload.getId() == null) {
			payload.setId(new ObjectId().toString());
		}
		
		EncryptionMaterialsProvider provider = new DirectKmsMaterialProvider(kms, keyId, null);
		DynamoDBMapper mapper = new DynamoDBMapper(dynamoDB, DynamoDBMapperConfig.DEFAULT, new AttributeEncryptor(provider));
		
		String json = objectMapper.writeValueAsString(payload);
		
		Event event = new Event().withEventDate(Date.from(Instant.now()))
				.withEventStatus(Event.EventStatus.NEW)
				.withType(payload.getClass().getName())
				.withOrganizationId("iidiis")
				.withPayload(json)
				.withUserId(null);
		
		mapper.save(event);
	}
}