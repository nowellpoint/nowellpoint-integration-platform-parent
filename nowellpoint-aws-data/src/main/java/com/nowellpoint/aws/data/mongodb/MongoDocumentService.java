package com.nowellpoint.aws.data.mongodb;

import static com.mongodb.client.model.Filters.eq;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import com.mongodb.Block;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.nowellpoint.aws.data.annotation.Document;

public abstract class MongoDocumentService<D extends MongoDocument> {
	
	private static final Logger LOGGER = Logger.getLogger(MongoDocumentService.class);
	
	private final Class<D> documentType;
	
	/**
	 * 
	 * @param resourceType
	 * @param documentType
	 */
	
	public MongoDocumentService(Class<D> documentType) {		
		this.documentType = documentType;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	protected D find(String id) {		
		String collectionName = documentType.getAnnotation(Document.class).collectionName();
		
		D document = null;
		try {
			document = MongoDatastore.getDatabase().getCollection( collectionName )
					.withDocumentClass( documentType )
					.find( eq ( "_id", new ObjectId( id ) ) )
					.first();
		} catch (IllegalArgumentException e) {
			
		}
		
		return document;
	}
	
	/**
	 * 
	 * @param subject
	 * @return
	 */
	
	protected Set<D> findAllByOwner(String subject) {		
		String collectionName = documentType.getAnnotation(Document.class).collectionName();
		
		FindIterable<D> search = MongoDatastore.getDatabase()
				.getCollection( collectionName )
				.withDocumentClass( documentType )
				.find( eq ( "owner.href", subject ) );
		
		Set<D> documents = new HashSet<D>();
		
		search.forEach(new Block<D>() {
			@Override
			public void apply(final D document) {
				documents.add(document);
		    }
		});
		
		return documents;
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return the created Document
	 * @throws MongoException
	 */
	
	protected D create(String subject, D document) {	
		document.setId(new ObjectId());
		document.setCreatedById(subject);
		document.setLastModifiedById(subject);
		document.setSystemCreationDate(Date.from(Instant.now()));
		document.setSystemModifiedDate(Date.from(Instant.now()));
		
		if (document.getCreatedDate() == null) {
			document.setCreatedDate(Date.from(Instant.now()));
			document.setLastModifiedDate(Date.from(Instant.now()));
		}
		
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
	 * @param document
	 * @return the document that has been replaced
	 * @throws MongoException
	 */
	
	protected D replace(String subject, D document) {
		
		document.setLastModifiedById(subject);
		document.setSystemModifiedDate(Date.from(Instant.now()));
		
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
	 * @param document
	 * @throws MongoException
	 */
	
	protected void delete(D document) {		
		try {
			MongoDatastore.deleteOne( document );
		} catch (MongoException e) {
			LOGGER.error( "Delete Document exception", e.getCause());
			throw e;
		}
	}
}