package com.nowellpoint.aws.data.mongodb;

import static com.mongodb.client.model.Filters.eq;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
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
	 * @param query
	 * @return
	 * 
	 * 
	 */
	
	protected Set<T> find(Bson query) {
		Set<T> documents = null;
		
		if (documents == null) {
			try {
				documents = MongoDatastore.find(documentClass, query);
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
	 * @param owner
	 * @return Collection of documents for owner
	 * 
	 * 
	 */
	
	protected Set<T> findAllByOwner(String owner) {		
		Set<T> documents = hscan(documentClass, encode(owner));
		
		if (documents == null || documents.isEmpty()) {
			documents = MongoDatastore.find(documentClass, eq ( "owner.href", owner ));
			hset( encode(owner), documents );
		}
		
		return documents; 
	}
	
	/**
	 * 
	 * 
	 * @param subject
	 * @param document
	 * @return
	 * 
	 * 
	 */
	
	protected T create(String subject, T document) {	
		document.setId(new ObjectId());
		document.setCreatedById(subject);
		document.setLastModifiedById(subject);
		document.setSystemCreationDate(Date.from(Instant.now()));
		document.setSystemModifiedDate(Date.from(Instant.now()));
		
		if (document.getCreatedDate() == null) {
			document.setCreatedDate(Date.from(Instant.now()));
			document.setLastModifiedDate(Date.from(Instant.now()));
		}
		
		set(document.getId().toString(), document);
		hset(encode(subject), document);
		
		try {
			MongoDatastore.insertOne( document );
		} catch (MongoException e) {
			LOGGER.error( "Create Document exception", e.getCause());
			throw e;
		}
		
		return document;		
	}
	
	/**
	 * 
	 * 
	 * @param subject
	 * @param document
	 * @return
	 * 
	 * 
	 */
	
	protected T replace(String subject, T document) {
		document.setLastModifiedById(subject);
		document.setLastModifiedDate(Date.from(Instant.now()));
		document.setSystemModifiedDate(Date.from(Instant.now()));
		
		set(document.getId().toString(), document);
		hset(encode(subject), document);
		
		try {
			MongoDatastore.replaceOne( document );
		} catch (MongoException e) {
			LOGGER.error( "Update Document exception", e.getCause());
			throw e;
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
	
	protected void delete(String subject, T document) {
		del(document.getId().toString());
		hdel(encode(subject), document);
		
		try {
			MongoDatastore.deleteOne( document );
		} catch (MongoException e) {
			LOGGER.error( "Delete Document exception", e.getCause());
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
	
	private static String encode(String src) {
		return Base64.getEncoder().encodeToString(src.getBytes());
	}
}