package com.nowellpoint.aws.api.service;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.lang.reflect.Type;
import java.net.URI;
import java.time.Clock;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.modelmapper.TypeToken;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.MongoCollection;
import com.nowellpoint.aws.api.data.MongoDBDatastore;
import com.nowellpoint.aws.api.dto.ApplicationDTO;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventAction;
import com.nowellpoint.aws.model.EventBuilder;
import com.nowellpoint.aws.model.data.Application;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class ApplicationService extends AbstractDataService<ApplicationDTO, Application> {
	
	private static final Logger LOGGER = Logger.getLogger(ApplicationService.class);
	
	private static final String COLLECTION_NAME = "applications";
	
	private DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	public ApplicationService() {
		super(ApplicationDTO.class, Application.class);
	}
	
	public Set<ApplicationDTO> getAll(String subject) {
		
		//
		//
		//
		
		Set<ApplicationDTO> resources = hscan( subject, ApplicationDTO.class );
		
		//
		//
		//
		
		if (resources.isEmpty()) {
			
			MongoCollection<Application> collection = MongoDBDatastore.getDatabase()
					.getCollection( COLLECTION_NAME )
					.withDocumentClass( Application.class );
				
			Set<Application> applications = StreamSupport.stream( collection.find( eq ( "owner", subject ) ).spliterator(), false )
						.collect( Collectors.toSet() );
			
			Type type = new TypeToken<Set<ApplicationDTO>>() {}.getType();
			
			resources = modelMapper.map( applications, type );
			
			hset( subject, resources );
		}
		
		//
		//
		//
		
		return resources;
		
	}
	
	public ApplicationDTO createIdentity(String subject, ApplicationDTO resource, URI eventSource) {
		
		//
		//
		//
		
		super.createIdentity(subject, resource, eventSource);
		
		//
		//
		//

		hset( subject, ApplicationDTO.class.getName().concat(resource.getId()), resource );
		hset( resource.getId(), subject, resource );
		
		//
		//
		//
		
		return resource;
	}
	
	public ApplicationDTO update(String subject, ApplicationDTO resource, URI eventSource) {
		
		//
		//
		//
		
		Application Application = modelMapper.map( resource, Application.class );
		Application.setLastModifiedDate(Date.from(Clock.systemUTC().instant()));
		Application.setLastModifiedById(subject);
		
		//
		//
		//
				
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubject(subject)
					.withEventAction(EventAction.UPDATE)
					.withEventSource(eventSource)
					.withPropertyStore(System.getenv("NCS_PROPERTY_STORE"))
					.withPayload(Application)
					.withType(Application.class)
					.build();
			
			mapper.save(event);
			
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e);
		}
				
		//
		//
		//
		
		modelMapper.map( Application, resource );
		
		//
		//
		//
		
		ApplicationDTO original = getApplication( resource.getId(), subject );
		resource.setCreatedById(original.getCreatedById());
		resource.setCreatedDate(original.getCreatedDate());
		
		//
		//
		//

		hset( subject, ApplicationDTO.class.getName().concat(resource.getId()), resource );
		hset( resource.getId(), subject, resource );

		//
		//
		//
		
		return resource;
	}
	
	public void delete(String id, String subject, URI eventSource) {
		
		//
		//
		//
			
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubject(subject)
					.withEventAction(EventAction.DELETE)
					.withEventSource(eventSource)
					.withPropertyStore(System.getenv("NCS_PROPERTY_STORE"))
					.withPayload(new Application(new ObjectId(id)))
					.withType(Application.class)
					.build();
			
			mapper.save( event );
			
		} catch (JsonProcessingException e) {
			LOGGER.error( "Delete Application exception", e.getCause() );
			throw new WebApplicationException( e, Status.INTERNAL_SERVER_ERROR );
		}
		
		//
		//
		//
		
		hdel( subject, ApplicationDTO.class.getName().concat(id) );
		hdel( id, subject );
	}
	
	public ApplicationDTO getApplication(String id, String subject) {
		
		//
		//
		//

		ApplicationDTO resource = hget( id, subject );
		
		//
		//
		//
		
		if ( resource == null ) {
			
			Application application = MongoDBDatastore.getDatabase().getCollection( COLLECTION_NAME )
					.withDocumentClass( Application.class )
					.find( and ( eq ( "_id", new ObjectId( id ) ), eq ( "owner", subject ) ) )
					.first();
			
			if ( application == null ) {
				throw new WebApplicationException( String.format( "Application Id: %s does not exist or you do not have access to view", id ), Status.NOT_FOUND );
			}
			
			resource = modelMapper.map( application, ApplicationDTO.class );

			hset( id, subject, resource );
		}
		
		//
		//
		//
		
		return resource;
	}
	
}