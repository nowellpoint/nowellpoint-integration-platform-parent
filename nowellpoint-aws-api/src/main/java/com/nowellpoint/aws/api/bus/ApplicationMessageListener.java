package com.nowellpoint.aws.api.bus;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.bson.Document;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import com.nowellpoint.aws.api.data.MongoDBDatastore;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventAction;
import com.nowellpoint.aws.model.EventStatus;
import com.nowellpoint.aws.model.data.Application;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class ApplicationMessageListener implements MessageListener {
	
	private static final String COLLECTION_NAME = "applications";
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
		String json = objectMapper.writeValueAsString(application);
		Document document = Document.parse(json);
		MongoDBDatastore.insertOne( COLLECTION_NAME, document );
	}
	
	private void update(Application application) throws JsonProcessingException {
		String json = objectMapper.writeValueAsString(application);
		Document document = Document.parse(json);
		document.remove("_id");		
		MongoDBDatastore.updateOne( COLLECTION_NAME, application.getId(), document );
	}
	
	private void delete(Application application) {
		MongoDBDatastore.deleteOne( COLLECTION_NAME, application.getId() );
	}
}