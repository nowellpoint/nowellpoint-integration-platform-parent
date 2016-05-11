package com.nowellpoint.aws.lambda.stream;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.nowellpoint.aws.data.dynamodb.Event;
import com.nowellpoint.aws.data.dynamodb.EventAction;
import com.nowellpoint.aws.data.dynamodb.EventStatus;
import com.nowellpoint.aws.event.AbstractEventHandler;
import com.nowellpoint.aws.event.AccountEventHandler;
import com.nowellpoint.aws.event.AccountProfileEventHandler;
import com.nowellpoint.aws.event.LeadEventHandler;
import com.nowellpoint.aws.event.SignUpEventHandler;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class EventHandler {
	
	private static Map<String,String> eventMapping = new HashMap<String,String>();
	
	static {
		eventMapping.put(EventAction.SIGN_UP.name(), SignUpEventHandler.class.getName());
		eventMapping.put(EventAction.ACCOUNT.name(), AccountEventHandler.class.getName());
		eventMapping.put(EventAction.LEAD.name(), LeadEventHandler.class.getName());
		eventMapping.put(EventAction.ACCOUNT_PROFILE.name(), AccountProfileEventHandler.class.getName());
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
			String subjectId = record.getDynamodb().getKeys().get("SubjectId").getS();
			
			//
			// retrieve the event record
			//
			
			Event event = mapper.load(Event.class, id, subjectId);
			
			//
			//
			//
			
			event.setStartTime(startTime);
			
			//
			// lookup properties for the event
			//
			
			Map<String, String> properties = Properties.getProperties(event.getPropertyStore());
			
			//
			// process the event
			//
			
			if (event.getEventStatus().equals(EventStatus.NEW.toString())) {
				try {
					if (eventMapping.containsKey(event.getEventAction())) {
						AbstractEventHandler handler = (AbstractEventHandler) Class.forName(eventMapping.get(event.getEventAction())).newInstance();
						handler.process(event, properties, context);
					} else {
						throw new IllegalArgumentException("Missing event mapping handler");
					}
				} catch (Exception e) {
					event.setErrorMessage(e.getMessage());
					event.setEventStatus(EventStatus.ERROR.toString());
				} finally {
					mapper.save(event);
				}
			}
		});
		
		return null;
	}
}