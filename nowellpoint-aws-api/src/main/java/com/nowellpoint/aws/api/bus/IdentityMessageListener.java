package com.nowellpoint.aws.api.bus;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.bson.Document;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import com.nowellpoint.aws.api.data.MongoDBDatastore;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventAction;
import com.nowellpoint.aws.model.EventStatus;
import com.nowellpoint.aws.model.data.Identity;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class IdentityMessageListener implements MessageListener {
	
	private static final String COLLECTION_NAME = "identities";
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();

	@Override
	public void onMessage(Message message) {
		
		if (message instanceof ObjectMessage) {
			try {
				Object object = ((ObjectMessage) message).getObject();
				Event event = ((Event) object);
				
				Identity identity = objectMapper.readValue(event.getPayload(), Identity.class); 
				
				try {
					if (EventAction.SIGN_UP.name().equals(event.getEventAction())) {
						signUp(identity);
					} else if (EventAction.CREATE.name().equals(event.getEventAction())) {
						create(identity);
					} else if (EventAction.UPDATE.name().equals(event.getEventAction())) {
						update(identity);
					} else if (EventAction.DELETE.name().equals(event.getEventAction())) {
						delete(identity);
					}
					event.setTargetId(identity.getId().toString());
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
	
	private void signUp(Identity identity) throws JsonProcessingException {
		Optional<Document> queryResult = Optional.ofNullable(MongoDBDatastore.getDatabase().getCollection( COLLECTION_NAME ).find( Filters.eq( "username", identity.getUsername() ) ).first());
		if ( queryResult.isPresent() ) {
			update(identity);
		} else {
			create(identity);
		}
	}
	
	private void create(Identity identity) throws JsonProcessingException {
		String json = objectMapper.writeValueAsString(identity);
		Document document = Document.parse(json);
		MongoDBDatastore.insertOne( COLLECTION_NAME, document );
	}
	
	private void update(Identity identity) throws JsonProcessingException {
		String json = objectMapper.writeValueAsString(identity);
		Document document = Document.parse(json);
		document.remove("_id");		
		MongoDBDatastore.updateOne( COLLECTION_NAME, identity.getId(), document );
	}
	
	private void delete(Identity identity) {
		MongoDBDatastore.deleteOne( COLLECTION_NAME, identity.getId() );
	}
}