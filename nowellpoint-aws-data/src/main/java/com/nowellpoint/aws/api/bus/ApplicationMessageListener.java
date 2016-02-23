package com.nowellpoint.aws.api.bus;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import com.nowellpoint.aws.data.MongoDBDatastore;
import com.nowellpoint.aws.data.model.Application;
import com.nowellpoint.aws.data.dynamodb.Event;
import com.nowellpoint.aws.data.dynamodb.EventAction;
import com.nowellpoint.aws.data.dynamodb.EventStatus;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class ApplicationMessageListener implements MessageListener {
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();

	@Override
	public void onMessage(Message message) {
		
		if (message instanceof ObjectMessage) {
			try {
				Object object = ((ObjectMessage) message).getObject();
				Event event = ((Event) object);
				
				Application application = objectMapper.readValue(event.getPayload(), Application.class); 
				
				try {
					if (EventAction.CREATE.name().equals(event.getEventAction())) {
						create(application);
					} else if (EventAction.UPDATE.name().equals(event.getEventAction())) {
						update(application);
					} else if (EventAction.DELETE.name().equals(event.getEventAction())) {
						delete(application);
					}
					event.setTargetId(application.getId().toString());
					event.setEventStatus(EventStatus.COMPLETE.toString());
				} catch (MongoException e) {
					event.setErrorMessage(e.getMessage());
					event.setEventStatus(EventStatus.ERROR.toString());
				} finally {
					event.setProcessedDate(Date.from(Instant.now()));
					event.setExecutionTime(System.currentTimeMillis() - event.getStartTime());
					mapper.save(event);
				}
				
				message.acknowledge();
			
			} catch (JMSException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void create(Application application) throws JsonProcessingException {
		MongoDBDatastore.insertOne( application );
	}
	
	private void update(Application application) throws JsonProcessingException {
		MongoDBDatastore.replaceOne( application );
	}
	
	private void delete(Application application) {
		MongoDBDatastore.deleteOne( application );
	}
}