package com.nowellpoint.aws.event;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.bson.Document;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.nowellpoint.aws.data.dynamodb.Event;
import com.nowellpoint.aws.data.dynamodb.EventStatus;
import com.nowellpoint.aws.data.mongodb.AccountProfile;
import com.nowellpoint.aws.model.admin.Properties;

public class AccountProfileEventHandler implements AbstractEventHandler {
	
	private static LambdaLogger logger;

	@Override
	public void process(Event event, Map<String, String> properties, Context context) {
		
		logger = context.getLogger();
		
		logger.log(this.getClass().getName() + " starting AccountProfileEventHandler");
		
		MongoClientURI mongoClientUri = new MongoClientURI("mongodb://".concat(properties.get(Properties.MONGO_CLIENT_URI)));
		MongoClient mongoClient = new MongoClient(mongoClientUri);
		
		try {
			MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoClientUri.getDatabase());
			
			AccountProfile identity = objectMapper.readValue(event.getPayload(), AccountProfile.class);
			
			String collectionName = AccountProfile.class.getAnnotation(com.nowellpoint.aws.data.annotation.Document.class).collectionName();
			
			Document document = null;
			
			Optional<Document> queryResult = Optional.ofNullable( mongoDatabase.getCollection( collectionName ).find( Filters.eq( "username", identity.getUsername() ) ).first() );
			
			if (queryResult.isPresent()) {
				identity.setCreatedById(null);
				identity.setLastModifiedDate(Date.from(Instant.now()));
				document = Document.parse( new ObjectMapper().writeValueAsString(identity) );
				mongoDatabase.getCollection( collectionName ).updateOne( Filters.eq ( "_id", queryResult.get().getObjectId("_id") ), new Document("$set", document ) );
				identity.setId(queryResult.get().getObjectId("_id"));
			} else {	
				Date now = Date.from(Instant.now());
				identity.setCreatedDate(now);
				identity.setLastModifiedDate(now);
				document = Document.parse( new ObjectMapper().writeValueAsString( identity ) );
				mongoDatabase.getCollection( collectionName ).insertOne( document );
				identity.setId(document.getObjectId("_id"));
			}
			
			event.setEventStatus(EventStatus.COMPLETE.name());
			event.setTargetId(identity.getId().toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			event.setEventStatus(EventStatus.ERROR.name());
			event.setErrorMessage(e.getMessage());
		} finally {
			mongoClient.close();
			event.setProcessedDate(Date.from(Instant.now()));
			event.setExecutionTime(System.currentTimeMillis() - event.getStartTime());
		}		
	}
}