package com.nowellpoint.aws.api.service;

import static com.mongodb.client.model.Filters.eq;

import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.nowellpoint.aws.api.dto.AbstractDTO;
import com.nowellpoint.aws.data.MongoDBDatastore;
import com.nowellpoint.aws.data.annotation.Handler;
import com.nowellpoint.aws.data.dynamodb.Event;
import com.nowellpoint.aws.data.dynamodb.EventAction;
import com.nowellpoint.aws.data.dynamodb.EventBuilder;
import com.nowellpoint.aws.data.mongodb.AbstractDocument;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

public abstract class AbstractDataService<R extends AbstractDTO, D extends AbstractDocument> extends AbstractCacheService {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractDataService.class);
	
	protected final ModelMapper modelMapper;
	
	private final DynamoDBMapper dynamoDBMapper;
	
	private final Class<R> resourceType;
	
	private final Class<D> documentType;
	
	/**
	 * 
	 * @param resourceType
	 * @param documentType
	 */
	
	public AbstractDataService(Class<R> resourceType, Class<D> documentType) {
		
		this.resourceType = resourceType;
		this.documentType = documentType;
		this.modelMapper = new ModelMapper();
		this.dynamoDBMapper = DynamoDBMapperProvider.getDynamoDBMapper();
		
		configureModelMapper(this.modelMapper);
		
	}
	
	/**
	 * 
	 * @param modelMapper
	 */
	
	private void configureModelMapper(ModelMapper modelMapper) {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		modelMapper.getConfiguration().setMethodAccessLevel(AccessLevel.PROTECTED); 
		modelMapper.addConverter(new AbstractConverter<String, ObjectId>() {
			protected ObjectId convert(String source) {
				return source == null ? null : new ObjectId(source);
			}
		});
		modelMapper.addConverter(new AbstractConverter<ObjectId, String>() {
			protected String convert(ObjectId source) {
				return source == null ? null : source.toString();
			}
		});
	}
	
	/**
	 * 
	 * @param subject
	 * @param id
	 * @return
	 */
	
	protected R find(String subject, String id) {		
		String collectionName = documentType.getAnnotation(Handler.class).collectionName();
		
		D document = MongoDBDatastore.getDatabase().getCollection( collectionName )
				.withDocumentClass( documentType )
				.find( eq ( "_id", new ObjectId( id ) ) )
				.first();

		if ( document == null ) {
			throw new WebApplicationException( String.format( "%s Id: %s does not exist or you do not have access to view", documentType.getSimpleName(), id ), Status.NOT_FOUND );
		}
		
		R resource = modelMapper.map( document, resourceType );
		
		return resource;
	}
	
	/**
	 * 
	 * @param subject
	 * @return
	 */
	
	protected Set<R> findAllByOwner(String subject) {		
		String collectionName = documentType.getAnnotation(Handler.class).collectionName();
		
		FindIterable<D> documents = MongoDBDatastore.getDatabase()
				.getCollection( collectionName )
				.withDocumentClass( documentType )
				.find( eq ( "owner", subject ) );
			
		Set<R> resources = new HashSet<R>();
		
		documents.forEach(new Block<D>() {
			@Override
			public void apply(final D document) {
		        resources.add(modelMapper.map( document, resourceType ));
		    }
		});
		
		return resources;
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return
	 */
	
	protected R create(String subject, R resource, URI eventSource) {
		AbstractDocument document = modelMapper.map( resource, documentType );
		
		document.setId(new ObjectId());
		document.setCreatedDate(Date.from(Instant.now()));
		document.setLastModifiedDate(Date.from(Instant.now()));
		document.setCreatedById(subject);
		document.setLastModifiedById(subject);
		
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubject(subject)
					.withEventAction(EventAction.CREATE)
					.withEventSource(eventSource)
					.withPropertyStore(System.getenv("NCS_PROPERTY_STORE"))
					.withPayload(document)
					.withType(document.getClass())
					.build();
			
			dynamoDBMapper.save( event );
			
		} catch (JsonProcessingException e) {
			LOGGER.error( "Create Document exception", e.getCause() );
			throw new WebApplicationException(e);
		}
		
		modelMapper.map( document, resource );
		
		return resource;		
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return
	 */
	
	protected R update(String subject, R resource, URI eventSource) {
		AbstractDocument document = modelMapper.map( resource, documentType );
		
		document.setLastModifiedDate(Date.from(Instant.now()));
		document.setLastModifiedById(subject);
		
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubject(subject)
					.withEventAction(EventAction.UPDATE)
					.withEventSource(eventSource)
					.withPropertyStore(System.getenv("NCS_PROPERTY_STORE"))
					.withPayload(document)
					.withType(document.getClass())
					.build();
			
			dynamoDBMapper.save(event);
			
		} catch (JsonProcessingException e) {
			LOGGER.error( "Update Document exception", e.getCause() );
			throw new WebApplicationException(e);
		}
				
		modelMapper.map( document, resource );
		
		return resource;
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 */
	
	protected void delete(String subject, R resource, URI eventSource) {		
		AbstractDocument document = modelMapper.map( resource, documentType );
		
		Event event = null;
		try {			
			event = new EventBuilder()
					.withSubject(subject)
					.withEventAction(EventAction.DELETE)
					.withEventSource(eventSource)
					.withPropertyStore(System.getenv("NCS_PROPERTY_STORE"))
					.withPayload(document)
					.withType(document.getClass())
					.build();
			
			dynamoDBMapper.save( event );
			
		} catch (JsonProcessingException e) {
			LOGGER.error( "Delete Document exception", e.getCause() );
			throw new WebApplicationException( e, Status.INTERNAL_SERVER_ERROR );
		}
	}
}