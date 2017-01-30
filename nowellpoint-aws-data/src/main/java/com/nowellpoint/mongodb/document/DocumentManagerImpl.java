package com.nowellpoint.mongodb.document;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;

public class DocumentManagerImpl extends AbstractDocumentManager implements DocumentManager {
	
	public DocumentManagerImpl(DocumentManagerFactory documentManagerFactory) {
		super(documentManagerFactory);
	}
	
	@Override
	public <T> String resolveCollectionName(Class<T> documentClass) {
		return resolveCollectionName(documentClass);
	}
	
	@Override
	public <T> Set<T> findAll(Class<T> documentClass) {
		MongoCollection<Document> collection = (MongoCollection<Document>) getCollection( documentClass );
		Set<T> objects = new HashSet<>();
		Set<Document> documents = findAll( collection );
		documents.forEach(document -> {
			T object = null;
			try {
				object = convertToObject(documentClass, document);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			objects.add(object);
		});
		
		return objects;
	}
	
	@Override
	public <T> T findOne(Class<T> documentClass, ObjectId id) {
		return findOne(documentClass, Filters.eq ( "_id", id ) );
	}
	
	@Override
	public <T> T findOne(Class<T> documentClass, Bson query) {
		MongoCollection<Document> collection = (MongoCollection<Document>) getCollection( documentClass );
		Document document = findOne(collection, query);
		
		if (document == null) {
			throw new DocumentNotFoundException(String.format( "Document of type: %s was not found: %s", documentClass.getSimpleName(), bsonToString(query) ) );
		}
		
		T object = null;
		try {
			object = convertToObject(documentClass, document);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return object;
	}
	
	@Override
	public <T> Set<T> find(Class<T> documentClass, Bson query) {
		MongoCollection<Document> collection = (MongoCollection<Document>) getCollection( documentClass );
		Set<T> objects = new HashSet<>();
		Set<Document> documents = find( collection, query );
		documents.forEach(document -> {
			T object = null;
			try {
				object = convertToObject(documentClass, document);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			objects.add(object);
		});
		
		return objects;
	}

	@Override
	public <T> void insertOne(T document) {
		MongoCollection<Document> collection = (MongoCollection<Document>) getCollection( document.getClass() );
		Document bson = convertToBson(document);
		insertOne(collection, bson);
		setIdValue(document, bson.get(ID));
	}
	
	@Override
	public <T> void upsert(Bson query, T document) {
		MongoCollection<Document> collection = (MongoCollection<Document>) getCollection( document.getClass() );
		Document bson = convertToBson(document);
		upsert(collection, bson, query);
		setIdValue(document, bson.get(ID));
	}
	
	@Override
	public <T> void replaceOne(T document) {
		Object id = resolveId(document);
		MongoCollection<Document> collection = (MongoCollection<Document>) getCollection( document.getClass() );
		Document bson = convertToBson(document);
		replaceOne( collection, bson, Filters.eq ( "_id", id ) );
	}
	
	@Override
	public <T> void deleteOne(T document) {
		Object id = resolveId(document);
		MongoCollection<Document> collection = (MongoCollection<Document>) getCollection( document.getClass() );
		deleteOne(  collection, Filters.eq ( ID, id ) );
	}
	
	@Override
	public <T> void deleteMany(Class<T> documentClass, Bson query) {
		MongoCollection<Document> collection = (MongoCollection<Document>) getCollection( documentClass );
		deleteMany( collection, query );
	}
}