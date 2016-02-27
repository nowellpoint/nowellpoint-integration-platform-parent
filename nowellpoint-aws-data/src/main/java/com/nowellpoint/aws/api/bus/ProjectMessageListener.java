package com.nowellpoint.aws.api.bus;

import static com.mongodb.client.model.Filters.eq;

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
import com.mongodb.DBRef;
import com.mongodb.MongoException;
import com.nowellpoint.aws.data.MongoDBDatastore;
import com.nowellpoint.aws.data.dynamodb.Event;
import com.nowellpoint.aws.data.dynamodb.EventAction;
import com.nowellpoint.aws.data.dynamodb.EventStatus;
import com.nowellpoint.aws.data.mongodb.Identity;
import com.nowellpoint.aws.data.mongodb.Project;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class ProjectMessageListener implements MessageListener {
	
	private DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();

	@Override
	public void onMessage(Message message) {
		
		if (message instanceof ObjectMessage) {
			try {
				Object object = ((ObjectMessage) message).getObject();
				Event event = ((Event) object);
				
				Project project = new ObjectMapper().readValue(event.getPayload(), Project.class); 
				
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
		String collectionName = MongoDBDatastore.getCollectionName( Identity.class );
		Identity owner = MongoDBDatastore.getDatabase()
				.getCollection( collectionName )
				.withDocumentClass( Identity.class )
				.find( eq ( "href", project.getOwner().getHref() ) )
				.first();
		project.getOwner().setIdentity(new DBRef(collectionName, owner.getId() ) );
		MongoDBDatastore.insertOne( project );
	}
	
	private void update(Project project) throws JsonProcessingException {
		MongoDBDatastore.replaceOne( project );
	}
	
	private void delete(Project project) {
		MongoDBDatastore.deleteOne( project );
	}
}