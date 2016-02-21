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
import com.nowellpoint.aws.model.data.Project;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class ProjectMessageListener implements MessageListener {
	
	private static final String COLLECTION_NAME = "projects";
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();

	@Override
	public void onMessage(Message message) {
		
		if (message instanceof ObjectMessage) {
			try {
				Object object = ((ObjectMessage) message).getObject();
				Event event = ((Event) object);
				
				Project project = objectMapper.readValue(event.getPayload(), Project.class); 
				
				try {
					if (EventAction.CREATE.name().equals(event.getEventAction())) {
						create(project);
					} else if (EventAction.UPDATE.name().equals(event.getEventAction())) {
						update(project);
					} else if (EventAction.DELETE.name().equals(event.getEventAction())) {
						delete(project);
					}
					event.setTargetId(project.getId().toString());
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
	
	private void create(Project project) throws JsonProcessingException {
		String json = objectMapper.writeValueAsString(project);
		Document document = Document.parse(json);
		MongoDBDatastore.insertOne( COLLECTION_NAME, document );
	}
	
	private void update(Project project) throws JsonProcessingException {
		String json = objectMapper.writeValueAsString(project);
		Document document = Document.parse(json);
		document.remove("_id");		
		MongoDBDatastore.updateOne( COLLECTION_NAME, project.getId(), document );
	}
	
	private void delete(Project project) {
		MongoDBDatastore.deleteOne( COLLECTION_NAME, project.getId() );
	}
}