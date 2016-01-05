package com.nowellpoint.aws.lambda.stream;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.nowellpoint.aws.event.AbstractEventHandler;
import com.nowellpoint.aws.event.AccountEventHandler;
import com.nowellpoint.aws.event.LeadEventHandler;
import com.nowellpoint.aws.event.OrganizationEventHandler;
import com.nowellpoint.aws.event.UserEventHandler;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.Lead;
import com.nowellpoint.aws.model.data.Organization;
import com.nowellpoint.aws.model.data.User;
import com.nowellpoint.aws.model.idp.Account;
import com.nowellpoint.aws.provider.ConfigurationProvider;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class EventHandler {
	
	private static Map<String,String> eventMapping = new HashMap<String,String>();
	
	static {
		eventMapping.put(User.class.getName(), UserEventHandler.class.getName());
		eventMapping.put(Account.class.getName(), AccountEventHandler.class.getName());
		eventMapping.put(Lead.class.getName(), LeadEventHandler.class.getName());
		eventMapping.put(Organization.class.getName(), OrganizationEventHandler.class.getName());
	}
	
	public String handleEvent(DynamodbEvent dynamodbEvent, Context context) {
		
		LambdaLogger logger = context.getLogger();
		
		System.setProperty("aws.kms.key.id", ConfigurationProvider.getAwsKmsKeyId());
		
		Predicate<DynamodbStreamRecord> insert = record -> "INSERT".equals(record.getEventName());
		
		dynamodbEvent.getRecords().stream().filter(insert).forEach(record -> {
			
			//
			// capture the start time
			//
			
			long startTime = System.currentTimeMillis();
			
			//
			// log the event
			//
			
			logger.log(new Date() + " Event received...Event Id: ".concat(record.getEventID()).concat(" Event Name: " + record.getEventName()));	
			
			//
			// get the keys from the event
			//
			
			String id = record.getDynamodb().getKeys().get("Id").getS();
			String organizationId = record.getDynamodb().getKeys().get("OrganizationId").getS();
			
			//
			// retrieve the event record
			//
			
			Event event = DynamoDBMapperProvider.getDynamoDBMapper().load(Event.class, id, organizationId);
			
			//
			// process the event
			//
			
			try {
				AbstractEventHandler handler = (AbstractEventHandler) (AbstractEventHandler) Class.forName(eventMapping.get(event.getType())).newInstance();
				handler.process(event, context);
				event.setEventStatus(Event.EventStatus.COMPLETE.toString());
			} catch (Exception e) {
				event.setErrorMessage(e.getMessage());
				event.setEventStatus(Event.EventStatus.ERROR.toString());
			} finally {
				event.setExecutionTime(System.currentTimeMillis() - startTime);
				event.setProcessedDate(Date.from(Instant.now()));
				DynamoDBMapperProvider.getDynamoDBMapper().save(event);
			}
		});
		
		return null;
	}
}