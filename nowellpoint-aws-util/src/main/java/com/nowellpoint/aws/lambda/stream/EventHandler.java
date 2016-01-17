package com.nowellpoint.aws.lambda.stream;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.nowellpoint.aws.event.AbstractEventHandler;
import com.nowellpoint.aws.event.AccountEventHandler;
import com.nowellpoint.aws.event.LeadEventHandler;
import com.nowellpoint.aws.event.OrganizationEventHandler;
import com.nowellpoint.aws.event.IdentityEventHandler;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventStatus;
import com.nowellpoint.aws.model.data.Organization;
import com.nowellpoint.aws.model.data.Identity;
import com.nowellpoint.aws.model.idp.Account;
import com.nowellpoint.aws.model.sforce.Lead;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class EventHandler {
	
	private static Map<String,String> eventMapping = new HashMap<String,String>();
	
	static {
		eventMapping.put(Identity.class.getName(), IdentityEventHandler.class.getName());
		eventMapping.put(Account.class.getName(), AccountEventHandler.class.getName());
		eventMapping.put(Lead.class.getName(), LeadEventHandler.class.getName());
		eventMapping.put(Organization.class.getName(), OrganizationEventHandler.class.getName());
	}
	
	public String handleEvent(DynamodbEvent dynamodbEvent, Context context) {
		
		//
		//
		//
		
		LambdaLogger logger = context.getLogger();
		
		//
		//
		//
		
		DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
		
		//
		//
		//
		
		Predicate<DynamodbStreamRecord> insert = record -> "INSERT".equals(record.getEventName());
		//Predicate<DynamodbStreamRecord> modify = record -> "MODIFY".equals(record.getEventName());
		
		//
		//
		//
		
		dynamodbEvent.getRecords().stream().filter(insert).forEach(record -> {
			
			//
			// capture the start time
			//
			
			long startTime = System.currentTimeMillis();
			
			//
			// log the event
			//
			
			logger.log("Event received...Event Id: ".concat(record.getEventID()).concat(" Event Name: " + record.getEventName()));	
			
			//
			// get the keys from the event
			//
			
			String id = record.getDynamodb().getKeys().get("Id").getS();
			String accountId = record.getDynamodb().getKeys().get("AccountId").getS();
			
			//
			// retrieve the event record
			//
			
			Event event = mapper.load(Event.class, id, accountId);
			
			//
			// process the event
			//
			
			if (event.getEventStatus().equals(EventStatus.NEW.toString())) {

				try {
					AbstractEventHandler handler = (AbstractEventHandler) (AbstractEventHandler) Class.forName(eventMapping.get(event.getType())).newInstance();
					handler.process(event, context);
					event.setEventStatus(EventStatus.COMPLETE.toString());
				} catch (Exception e) {
					event.setErrorMessage(e.getMessage());
					event.setEventStatus(EventStatus.ERROR.toString());
				} finally {
					event.setExecutionTime(System.currentTimeMillis() - startTime);
					event.setProcessedDate(Date.from(Instant.now()));
					mapper.save(event);
				}
			}
		});
		
		return null;
	}
}