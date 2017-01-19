package com.nowellpoint.mongodb.document;

import static com.mongodb.client.model.Filters.eq;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.Executors;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.nowellpoint.aws.data.AbstractCacheService;

public abstract class MongoDocumentService<T extends MongoDocument> extends AbstractCacheService {
	
	protected static final ObjectMapper objectMapper = new ObjectMapper();
	
	private static final Logger LOGGER = Logger.getLogger(MongoDocumentService.class);
	
	private final Class<T> documentClass;
	
	/**
	 * 
	 * 
	 * @param documentClass
	 * 
	 * 
	 */
	
	public MongoDocumentService(Class<T> documentClass) {		
		this.documentClass = documentClass;
	}
	
	/**
	 * 
	 * 
	 * @return
	 * 
	 * 
	 */
	
	public String getCollectionName() {
		return MongoDatastore.getCollectionName( documentClass );
	}
	
	/**
	 * 
	 * 
	 * @param query
	 * @return
	 * 
	 * 
	 */
	
	public FindIterable<T> query(Bson query) {
		
		FindIterable<T> search = null;
		
		try {
			
			search = MongoDatastore.getCollection( documentClass )
					.withDocumentClass( documentClass )
					.find( query );
			
		} catch (IllegalArgumentException e) {
			LOGGER.error( "query exception : ", e.getCause() );
		}
		
		return search;
	}
	
	/**
	 * 
	 * 
	 * @param query
	 * @return
	 * 
	 * 
	 */
	
	public T findOne(Bson query) {
		
		T document = null;
		
		try {
			
			document = MongoDatastore.getCollection(documentClass)
					.withDocumentClass( documentClass )
					.find( query )
					.first();
			
		} catch (IllegalArgumentException e) {
			LOGGER.error( "findOne exception : ", e.getCause());
		}
		
		if (document == null) {
			throw new DocumentNotFoundException(String.format( "Document of type: %s was not found: %s", documentClass.getSimpleName(), toString(query) ) );
		}
		
		return document;
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @return
	 * 
	 * 
	 */
	
	public T fetch(String id) {	
		
		T document = get(documentClass, id);
		
		if (document == null) {
			try {
				
				document = MongoDatastore.getCollection( documentClass )
						.withDocumentClass( documentClass )
						.find( eq ( "_id", new ObjectId( id ) ) )
						.first();
				
				if (document == null) {
					throw new DocumentNotFoundException(String.format( "Resource of type: %s for Id: %s was not found", documentClass.getSimpleName(), id.toString() ) );
				}
				
				set(id, document);
				
			} catch (IllegalArgumentException e) {
				LOGGER.error( "fetch exception : ", e.getCause());
			}
		} 

		return document;
	}
	
	/**
	 * 
	 * 
	 * @param document
	 * 
	 * 
	 */
	
	public void create(T document) {	
		
		Date now = Date.from(Instant.now());
		
		document.setId(new ObjectId());
		document.setSystemCreatedDate(now);
		document.setSystemModifiedDate(now);
		
		if (document.getCreatedDate() == null) {
			document.setCreatedDate(now);
			document.setLastModifiedDate(now);
		}
		
		set(document.getId().toString(), document);
		
		try {

			Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {
					MongoDatastore.getCollection( document ).insertOne( document );
				}
			});
			
		} catch (MongoException e) {
			LOGGER.error( "Create Document exception", e.getCause());
			publish(e);
			throw e;
		}	
	}
	
	/**
	 * 
	 * 
	 * @param document
	 * 
	 * 
	 */
	
	public void replace(T document) {
		
		Date now = Date.from(Instant.now());
		
		document.setLastModifiedDate(now);
		document.setSystemModifiedDate(now);
		
		set(document.getId().toString(), document);
		
		try {
			
			Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {
					MongoDatastore.getCollection( document ).replaceOne( Filters.eq ( "_id", document.getId() ), document );
				}
			});
			
		} catch (MongoException e) {
			LOGGER.error( "Update Document exception", e.getCause());
			publish(e);
			throw e;
		}
	}
	
	/**
	 * 
	 * 
	 * @param document
	 * 
	 * 
	 */
	
	public void delete(T document) {
		
		del(document.getId().toString());
		
		try {
			
			Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {
					MongoDatastore.getCollection( document ).deleteOne(  Filters.eq ( "_id", document.getId() ) );
				}
			});
			
		} catch (MongoException e) {
			LOGGER.error( "Delete Document exception", e.getCause() );
			publish(e);
			throw e;
		}
	}
	
	/**
	 * 
	 *
	 * @param collectionName
	 * @param query
	 * 
	 * 
	 */
	
	public void deleteMany(String collectionName, Bson query) {
		
		try {
			
			Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {
					MongoDatastore.getDatabase().getCollection( collectionName ).deleteMany( query );
				}
			});
		
		} catch (MongoException e) {
			LOGGER.error( "Delete Document exception", e.getCause() );
			publish(e);
			throw e;
		}
		
	}
	
	/**
	 * 
	 * 
	 * @param src
	 * @return
	 * 
	 * 
	 */
	
	public static String encode(String src) {
		return Base64.getEncoder().encodeToString(src.getBytes());
	}
	
	/**
	 * 
	 * 
	 * @param bson
	 * @return
	 * 
	 * 
	 */
	
	private String toString(Bson bson) {
		return bson.toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry()).toString();
	}
	
	/**
	 * 
	 * 
	 * @param exception
	 * 
	 * 
	 */
	
	private static void publish(MongoException exception) {
		AmazonSNS snsClient = new AmazonSNSClient();
		PublishRequest publishRequest = new PublishRequest("arn:aws:sns:us-east-1:600862814314:MONGODB_EXCEPTION", exception.getMessage());
		snsClient.publish(publishRequest);
	}
}