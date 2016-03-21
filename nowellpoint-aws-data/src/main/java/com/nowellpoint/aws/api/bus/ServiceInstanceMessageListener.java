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
import com.nowellpoint.aws.data.dynamodb.Event;
import com.nowellpoint.aws.data.dynamodb.EventAction;
import com.nowellpoint.aws.data.dynamodb.EventStatus;
import com.nowellpoint.aws.data.mongodb.ServiceInstance;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class ServiceInstanceMessageListener implements MessageListener {
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();

	@Override
	public void onMessage(Message message) {
		
		if (message instanceof ObjectMessage) {
			try {
				Object object = ((ObjectMessage) message).getObject();
				Event event = ((Event) object);
				
				ServiceInstance serviceInstance = objectMapper.readValue(event.getPayload(), ServiceInstance.class); 
				
				try {
					if (EventAction.CREATE.name().equals(event.getEventAction())) {
						create(serviceInstance);
					} else if (EventAction.UPDATE.name().equals(event.getEventAction())) {
						update(serviceInstance);
					} else if (EventAction.DELETE.name().equals(event.getEventAction())) {
						delete(serviceInstance);
					}
					event.setTargetId(serviceInstance.getId().toString());
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
	
	private void create(ServiceInstance serviceInstance) throws JsonProcessingException {
		MongoDBDatastore.insertOne( serviceInstance );
	}
	
	private void update(ServiceInstance serviceInstance) throws JsonProcessingException {
		MongoDBDatastore.replaceOne( serviceInstance );
	}
	
	private void delete(ServiceInstance serviceInstance) {
		MongoDBDatastore.deleteOne( serviceInstance );
	}
}