package com.nowellpoint.mongodb.document;

import static com.mongodb.client.model.Filters.eq;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
	
	protected String getCollectionName() {
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
	
	protected Set<T> query(Bson query) {
		Set<T> documents = new HashSet<T>();
		
		if (documents.isEmpty()) {
			try {
				//documents = MongoDatastore.find(documentClass, query);
			} catch (IllegalArgumentException e) {
				LOGGER.error( "Find exception : ", e.getCause() );
			}
		}
		
		/**
		 * String collectionName = getCollectionName( documentClass );
		
		Set<T> documents = new HashSet<T>();
		
		FindIterable<T> search = getDatabase()
				.getCollection( collectionName )
				.withDocumentClass( documentClass )
				.find( query );
		
		search.forEach(new Block<T>() {
			@Override
			public void apply(final T document) {
				documents.add(document);
		    }
		});
		
		return documents;
		 */
		
		return documents;
	}
	
	/**
	 * 
	 * 
	 * @param query
	 * @return
	 * 
	 * 
	 */
	
	protected T findOne(Bson query) {
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
	
	protected T fetch(String id) {	
		T document = get(documentClass, id);
		
		if (document == null) {
			try {
				
				document = MongoDatastore.getCollection( document )
						.withDocumentClass( documentClass )
						.find( eq ( "_id", id ) )
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
	
	protected void create(T document) {	
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
	
	protected void replace(T document) {
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
	
	protected void delete(T document) {
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
	
	protected void deleteMany(String collectionName, Bson query) {
		
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
	
	protected static String encode(String src) {
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