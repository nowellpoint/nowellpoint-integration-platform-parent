package com.nowellpoint.aws.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

import org.bson.types.ObjectId;

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

public class AbstractPayload implements Serializable {
	
	private static final long serialVersionUID = 4644224121071606758L;
	private static ObjectMapper objectMapper = new ObjectMapper();
	private static AmazonDynamoDBClient client = new AmazonDynamoDBClient();
	private static AWSKMS kms = new AWSKMSClient();
	
	private String id;
	
	public AbstractPayload() {
		
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public void save() throws JsonProcessingException {
		String keyId = System.getenv("AWS_KMS_KEY_ID");
		
		setId(new ObjectId().toString());
		
		EncryptionMaterialsProvider provider = new DirectKmsMaterialProvider(kms, keyId, null);
		DynamoDBMapper mapper = new DynamoDBMapper(client, DynamoDBMapperConfig.DEFAULT, new AttributeEncryptor(provider));
		
		String json = objectMapper.writeValueAsString(this);
		
		Event event = new Event().withEventDate(Date.from(Instant.now()))
				.withEventStatus(Event.EventStatus.NEW)
				.withType(getClass().getName())
				.withOrganizationId("iidiis")
				.withPayload(json)
				.withUserId(null);
		
		mapper.save(event);
	}
}