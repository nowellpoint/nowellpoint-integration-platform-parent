package com.nowellpoint.mongodb.document;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
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
	
	protected Set<T> find(Bson query) {
		Set<T> documents = new HashSet<T>(); //hscan(documentClass, encode(toString(query)));
		
		if (documents.isEmpty()) {
			try {
				documents = MongoDatastore.find(documentClass, query);
				//hset(encode(toString(query)), documents);
			} catch (IllegalArgumentException e) {
				LOGGER.error( "Find exception : ", e.getCause() );
			}
		}
		
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
		
		if (document == null) {
			try {
				document = MongoDatastore.findOne(documentClass, query);
			} catch (IllegalArgumentException e) {
				LOGGER.error( "FindOne exception : ", e.getCause());
			}
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
	
	protected T findById(String id) {	
		T document = get(documentClass, id);
		
		if (document == null) {
			try {
				document = MongoDatastore.findById(documentClass, new ObjectId(id));
				set(id, document);
			} catch (IllegalArgumentException e) {
				LOGGER.error( "FindById exception : ", e.getCause());
			}
		}

		return document;
	}
	
	/**
	 * 
	 * 
	 * @param subject
	 * @param document
	 * 
	 * 
	 */
	
	protected void create(String subject, T document) {	
		Date now = Date.from(Instant.now());
		
		document.setId(new ObjectId());
		document.setCreatedById(subject);
		document.setLastModifiedById(subject);
		document.setSystemCreationDate(now);
		document.setSystemModifiedDate(now);
		
		if (document.getCreatedDate() == null) {
			document.setCreatedDate(now);
			document.setLastModifiedDate(now);
		}
		
		set(document.getId().toString(), document);
		hset(encode(subject), document);

		try {
			MongoDatastore.insertOne( document );
		} catch (MongoException e) {
			LOGGER.error( "Create Document exception", e.getCause());
			throw e;
		}		
	}
	
	/**
	 * 
	 * 
	 * @param document
	 * @return
	 * 
	 * 
	 */
	
	protected void create(T document) {	
		Date now = Date.from(Instant.now());
		
		document.setId(new ObjectId());
		document.setSystemCreationDate(now);
		document.setSystemModifiedDate(now);
		
		if (document.getCreatedDate() == null) {
			document.setCreatedDate(now);
			document.setLastModifiedDate(now);
		}
		
		set(document.getId().toString(), document);
		
		try {
			MongoDatastore.insertOne( document );
		} catch (MongoException e) {
			LOGGER.error( "Create Document exception", e.getCause());
			throw e;
		}	
	}
	
	/**
	 * 
	 * 
	 * @param subject
	 * @param document
	 * 
	 * 
	 */
	
	protected void replace(String subject, T document) {
		Date now = Date.from(Instant.now());
		
		document.setLastModifiedById(subject);
		document.setLastModifiedDate(now);
		document.setSystemModifiedDate(now);
		
		set(document.getId().toString(), document);
		hset(encode(subject), document);
		
		try {
			MongoDatastore.replaceOne( document );
		} catch (MongoException e) {
			LOGGER.error( "Update Document exception", e.getCause());
			throw e;
		}
	}
	
	/**
	 * 
	 * 
	 * @param subject
	 * @param document
	 * 
	 * 
	 */
	
	protected void replace(String subject, ObjectId id, T document) {
		Date now = Date.from(Instant.now());
		
		document.setLastModifiedById(subject);
		document.setLastModifiedDate(now);
		document.setSystemModifiedDate(now);
		
		set(document.getId().toString(), document);
		hset(encode(subject), document);
		
		try {
			MongoDatastore.replaceOne( id, document );
		} catch (MongoException e) {
			LOGGER.error( "Update Document exception", e.getCause());
			throw e;
		}
	}
	
	/**
	 * 
	 * 
	 * @param filter
	 * @param document
	 * 
	 * 
	 */
	
	protected void replace(Bson filter, T document) {
		Date now = Date.from(Instant.now());
		
		document.setSystemModifiedDate(now);
		
		set(document.getId().toString(), document);
		
		try {
			MongoDatastore.replaceOne( filter, document );
		} catch (MongoException e) {
			LOGGER.error( "Update Document exception", e.getCause() );
			throw e;
		}
	}
	
	/**
	 * 
	 * 
	 * @param subject
	 * @param document
	 * 
	 * 
	 */
	
	protected void delete(String subject, T document) {
		del(document.getId().toString());
		hdel(encode(subject), document);
		
		try {
			MongoDatastore.deleteOne( document );
		} catch (MongoException e) {
			LOGGER.error( "Delete Document exception", e.getCause() );
			throw e;
		}
	}
	
	/**
	 * 
	 * @param document
	 */
	
	protected void delete(T document) {		
		try {
			MongoDatastore.deleteOne( document );
		} catch (MongoException e) {
			LOGGER.error( "Delete Document exception", e.getCause() );
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
	
	@SuppressWarnings("unused")
	private String toString(Bson bson) {
		return bson.toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry()).toString();
	}
}