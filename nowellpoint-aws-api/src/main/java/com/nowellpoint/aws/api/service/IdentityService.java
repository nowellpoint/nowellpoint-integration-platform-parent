package com.nowellpoint.aws.api.service;

import static com.mongodb.client.model.Filters.eq;

import java.net.URI;
import java.time.Clock;
import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nowellpoint.aws.api.data.Datastore;
import com.nowellpoint.aws.api.dto.IdentityDTO;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventAction;
import com.nowellpoint.aws.model.EventBuilder;
import com.nowellpoint.aws.model.data.Identity;
import com.nowellpoint.aws.model.data.Project;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public class IdentityService extends AbstractDataService {
	
	private static final Logger LOGGER = Logger.getLogger(IdentityService.class);
	
	private static final String COLLECTION_NAME = "identities";
	
	private DynamoDBMapper mapper = DynamoDBMapperProvider.getDynamoDBMapper();
	
	@PostConstruct
	public void postConstruct() {
		
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return the created Identity resource
	 */
	
	public IdentityDTO create(String subject, IdentityDTO resource, URI eventSource) {
		
		//
		//
		//
		
		Identity identity = modelMapper.map( resource, Identity.class );
		
		//
		//
		//
		
		identity.setId(UUID.randomUUID().toString());
		identity.setCreatedDate(Date.from(Clock.systemUTC().instant()));
		identity.setLastModifiedDate(Date.from(Clock.systemUTC().instant()));
		identity.setCreatedById(subject);
		identity.setLastModifiedById(subject);
		identity.setHref(subject);
		
		//
		//
		//
				
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubject(subject)
					.withEventAction(EventAction.CREATE)
					.withEventSource(eventSource)
					.withPropertyStore(System.getenv("NCS_PROPERTY_STORE"))
					.withPayload(identity)
					.withType(Identity.class)
					.build();
			
			mapper.save( event );
			
		} catch (JsonProcessingException e) {
			LOGGER.error( "Create Project exception", e.getCause() );
			throw new WebApplicationException(e);
		}
		
		//
		//
		//
		
		modelMapper.map( identity, resource );
		
		//
		//
		//

		//set( resource.getId(), resource );
		
		//
		//
		//
		
		return resource;
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return the updated Identity resource
	 */
	
	public IdentityDTO update(String subject, IdentityDTO resource, URI eventSource) {
		
		//
		//
		//
		
		IdentityDTO original = getIdentity( resource.getId(), subject );
		resource.setCreatedById(original.getCreatedById());
		resource.setCreatedDate(original.getCreatedDate());
		
		//
		//
		//
		
		Identity identity = modelMapper.map( resource, Identity.class );
		identity.setLastModifiedDate(Date.from(Clock.systemUTC().instant()));
		identity.setLastModifiedById(subject);
		
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
					.withPayload(identity)
					.withType(Project.class)
					.build();
			
			mapper.save(event);
			
		} catch (JsonProcessingException e) {
			throw new WebApplicationException(e);
		}
				
		//
		//
		//
		
		modelMapper.map( identity, resource );
		
		
		//
		//
		//

		//hset( subject, IdentityDTO.class.getName().concat(resource.getId()), resource );
		//set( resource.getId(), resource );

		//
		//
		//
		
		return resource;
	}
	
	/**
	 * 
	 * @param id
	 * @return Identity resource for id
	 */
	
	public IdentityDTO getIdentity(String id, String subject) {
		
		//
		//
		//

		//IdentityDTO resource = get( id );
		
		//
		//
		//
		
		//if ( resource == null ) {
			
			Identity identity = Datastore.getDatabase().getCollection( COLLECTION_NAME )
					.withDocumentClass( Identity.class )
					.find( eq ( "_id", id ) )
					.first();
			
			if ( identity == null ) {
				throw new WebApplicationException( String.format( "Identity Id: %s does not exist or you do not have access to view", id ), Status.NOT_FOUND );
			}
			
			IdentityDTO resource = modelMapper.map( identity, IdentityDTO.class );

			//hset( subject, IdentityDTO.class.getName().concat(resource.getId()), resource );
		//}
		
		//
		//
		//
		
		return resource;
	}
	
	/**
	 * 
	 * @param subject
	 * @return Identity resource for subject
	 */
	
	public IdentityDTO getIdentityBySubject(String subject) {
		
		//
		//
		//

		//IdentityDTO resource = hget( id, subject );
		
		//
		//
		//
		
		//if ( resource == null ) {
			
			Identity identity = Datastore.getDatabase().getCollection( COLLECTION_NAME )
					.withDocumentClass( Identity.class )
					.find( eq ( "href", subject ) )
					.first();
			
			if ( identity == null ) {
				throw new WebApplicationException( String.format( "Identity for subject: %s does not exist or you do not have access to view", subject ), Status.NOT_FOUND );
			}
			
			IdentityDTO resource = modelMapper.map( identity, IdentityDTO.class );

			//hset( subject, IdentityDTO.class.getName().concat(resource.getId()), resource );
		//}
		
		//
		//
		//
		
		return resource;
		
	}
}