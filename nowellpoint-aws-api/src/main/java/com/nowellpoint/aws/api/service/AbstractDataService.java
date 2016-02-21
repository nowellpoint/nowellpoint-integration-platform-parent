package com.nowellpoint.aws.api.service;

import static com.mongodb.client.model.Filters.eq;
import static com.nowellpoint.aws.api.data.CacheManager.deserialize;
import static com.nowellpoint.aws.api.data.CacheManager.serialize;
import static redis.clients.jedis.ScanParams.SCAN_POINTER_START;

import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
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
import com.nowellpoint.aws.api.data.CacheManager;
import com.nowellpoint.aws.api.data.MongoDBDatastore;
import com.nowellpoint.aws.api.dto.AbstractDTO;
import com.nowellpoint.aws.model.Event;
import com.nowellpoint.aws.model.EventAction;
import com.nowellpoint.aws.model.EventBuilder;
import com.nowellpoint.aws.model.annotation.Handler;
import com.nowellpoint.aws.model.data.AbstractDocument;
import com.nowellpoint.aws.provider.DynamoDBMapperProvider;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public abstract class AbstractDataService<R extends AbstractDTO, D extends AbstractDocument> {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractDataService.class);
	
	@Inject
	private CacheManager cacheManager;
	
	protected final ModelMapper modelMapper;
	
	private final DynamoDBMapper dynamoDBMapper;
	
	private final Class<R> resourceType;
	
	private final Class<D> documentType;
	
	public AbstractDataService(Class<R> resourceType, Class<D> documentType) {
		
		this.resourceType = resourceType;
		this.documentType = documentType;
		this.modelMapper = new ModelMapper();
		this.dynamoDBMapper = DynamoDBMapperProvider.getDynamoDBMapper();
		
		configureModelMapper(this.modelMapper);		
		
	}
	
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
	 * @param key
	 * @param value
	 */
	
	protected void set(String key, Object value) {
		Jedis jedis = cacheManager.getCache();
		jedis.set(key.getBytes(), serialize(value));
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	
	protected R get(String key) {
		Jedis jedis = cacheManager.getCache();
		byte[] bytes = jedis.get(key.getBytes());
		R value = null;
		if (bytes != null) {
			value = deserialize(bytes);
		}
		return value;
	}
	
	/**
	 * 
	 * @param key
	 * @param field
	 * @param value
	 */
	
	protected <T extends R> void hset(String key, String field, T value) {
		Jedis jedis = cacheManager.getCache();
		jedis.hset(key.getBytes(), field.getBytes(), serialize(value));
	}
	
	/**
	 * 
	 * @param key
	 * @param field
	 */
	
	protected void hdel(String key, String field) {
		Jedis jedis = cacheManager.getCache();
		jedis.hdel(key.getBytes(), field.getBytes());
	}
	
	/**
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	
	public <T extends R> T hget(String key, String field) {
		Jedis jedis = cacheManager.getCache();
		byte[] bytes = jedis.hget(key.getBytes(), field.getBytes());
		T value = null;
		if (bytes != null) {
			value = deserialize(bytes);
		}
		return value;
	}
	
	/**
	 * 
	 * @param key
	 * @param values
	 */
	
	protected <T extends R> void hset(String key, Set<T> values) {
		Jedis jedis = cacheManager.getCache();
		Pipeline p = jedis.pipelined();		
		values.stream().forEach(value -> {
			try {
				p.hset(key.getBytes(), value.getClass().getName().concat(value.getId()).getBytes(), serialize(value));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		p.sync();
	}
	
	/**
	 * 
	 * @param key
	 * @param type
	 * @return
	 */
	
	protected <T  extends R> Set<T> hscan(String key, Class<T> type) {
		Jedis jedis = cacheManager.getCache();
		ScanParams params = new ScanParams();
	    params.match(type.getName().concat("*"));
		
		ScanResult<Entry<byte[], byte[]>> scanResult = jedis.hscan(key.getBytes(), SCAN_POINTER_START.getBytes(), params);
		
		Set<T> results = new HashSet<T>();
		
		scanResult.getResult().forEach(r -> {
			T t = deserialize(r.getValue());
			results.add(t);
		});
		
		return results;
	}
	
	/**
	 * 
	 * @param subject
	 * @param id
	 * @return
	 */
	
	protected R find(String subject, String id) {
		
		//
		//
		//
		
		String collectionName = documentType.getAnnotation(Handler.class).collectionName();
		
		//
		//
		//
		
		D document = MongoDBDatastore.getDatabase().getCollection( collectionName )
				.withDocumentClass( documentType )
				.find( eq ( "_id", new ObjectId( id ) ) )
				.first();

		if ( document == null ) {
			throw new WebApplicationException( String.format( "%s Id: %s does not exist or you do not have access to view", documentType.getSimpleName(), id ), Status.NOT_FOUND );
		}
		
		//
		//
		//
		
		R resource = modelMapper.map( document, resourceType );
		
		//
		//
		//
		
		return resource;
	}
	
	/**
	 * 
	 * @param subject
	 * @return
	 */
	
	protected Set<R> findAllByOwner(String subject) {
		
		//
		//
		//
		
		String collectionName = documentType.getAnnotation(Handler.class).collectionName();
		
		//
		//
		//
		
		FindIterable<D> documents = MongoDBDatastore.getDatabase()
				.getCollection( collectionName )
				.withDocumentClass( documentType )
				.find( eq ( "owner", subject ) );
			
		//
		//
		//
		
		Set<R> resources = new HashSet<R>();
		
		//
		//
		//
//		
		documents.forEach(new Block<D>() {
			@Override
			public void apply(final D document) {
		        resources.add(modelMapper.map( document, resourceType ));
		    }
		});
		
		//
		//
		//
		
		return resources;
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return
	 */
	
	protected R createIdentity(String subject, R resource, URI eventSource) {
		
		//
		//
		//
		
		AbstractDocument document = modelMapper.map( resource, documentType );
		
		//
		//
		//
		
		document.setId(new ObjectId());
		document.setCreatedDate(Date.from(Instant.now()));
		document.setLastModifiedDate(Date.from(Instant.now()));
		document.setCreatedById(subject);
		document.setLastModifiedById(subject);
		
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
					.withPayload(document)
					.withType(document.getClass())
					.build();
			
			dynamoDBMapper.save( event );
			
		} catch (JsonProcessingException e) {
			LOGGER.error( "Create Document exception", e.getCause() );
			throw new WebApplicationException(e);
		}
		
		//
		//
		//
		
		modelMapper.map( document, resource );
		
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
	 * @return
	 */
	
	protected R update(String subject, R resource, URI eventSource) {
		
		//
		//
		//
		
		AbstractDocument document = modelMapper.map( resource, documentType );
		
		//
		//
		//
		
		document.setLastModifiedDate(Date.from(Instant.now()));
		document.setLastModifiedById(subject);
		
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
					.withPayload(document)
					.withType(document.getClass())
					.build();
			
			dynamoDBMapper.save(event);
			
		} catch (JsonProcessingException e) {
			LOGGER.error( "Update Document exception", e.getCause() );
			throw new WebApplicationException(e);
		}
				
		//
		//
		//
		
		modelMapper.map( document, resource );
		
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
	 */
	
	protected void delete(String subject, R resource, URI eventSource) {
		
		//
		//
		//
		
		AbstractDocument document = modelMapper.map( resource, documentType );
		
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