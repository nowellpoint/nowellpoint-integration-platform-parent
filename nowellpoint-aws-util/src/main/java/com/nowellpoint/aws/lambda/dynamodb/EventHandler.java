package com.nowellpoint.aws.lambda.dynamodb;

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
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.model.Configuration;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.Lead;

public class EventHandler {
	
	private static AmazonDynamoDB dynamoDB = new AmazonDynamoDBClient();
	private static AWSKMS kms = new AWSKMSClient();
	private static DynamoDBMapper mapper;
	private static ObjectMapper objectMapper;
	
	public EventHandler() {
		EncryptionMaterialsProvider provider = new DirectKmsMaterialProvider(kms, Configuration.getAwsKmsKeyId(), null);
		mapper = new DynamoDBMapper(dynamoDB, DynamoDBMapperConfig.DEFAULT, new AttributeEncryptor(provider));
		objectMapper = new ObjectMapper();
	}
	
	public String handleEvent(DynamodbEvent dynamodbEvent, Context context) {
		
		LambdaLogger logger = context.getLogger();
		
		dynamodbEvent.getRecords().stream().filter(record -> "INSERT".equals(record.getEventName())).forEach(record -> {
			
			/**
			 * 
			 */
			
			logger.log(new Date() + " Event received...Event Id: "
					.concat(record.getEventID())
					.concat(" Event Name: " + record.getEventName()));	
			
			String id = record.getDynamodb().getKeys().get("Id").getS();
			String organizationId = record.getDynamodb().getKeys().get("OrganizationId").getS();
			
			Event event = mapper.load(Event.class, id, organizationId);
			
			try {
				if (Lead.class.getName().equals(event.getType())) {
					Lead lead = objectMapper.readValue(event.getPayload(), Lead.class);
					logger.log("First Name: " + lead.getFirstName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		return null;
	}
}