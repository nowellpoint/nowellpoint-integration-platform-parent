package com.nowellpoint.mongodb.document;

import java.util.HashSet;
import java.util.Set;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;

public class DocumentManagerImpl extends AbstractDocumentManager implements DocumentManager {
	
	public DocumentManagerImpl(DocumentManagerFactory documentManagerFactory) {
		super(documentManagerFactory);
	}
	
	/**
	 * Returns the collection name for the class that has been 
	 * annotated as a Document for the {@link Class<T>}.
	 * 
	 * @return the Collection name for {@link Class<T>}
	 */
	
	@Override
	public <T> String resolveCollectionName(Class<T> documentClass) {
		return resolveCollectionName(documentClass);
	}
	
	@Override
	public <T> Set<T> findAll(Class<T> documentClass) {
		MongoCollection<Document> collection = getCollection( documentClass );
		Set<T> list = new HashSet<>();
		Set<Document> documents = findAll( collection );
		documents.forEach(document -> {
			T object = toObject(documentClass, document);
			list.add(object);
		});
		return list;
	}
	
	@Override
	public <T> T fetch(Class<T> documentClass, ObjectId id) {
		return findOne(documentClass, Filters.eq ( "_id", id ) );
	}
	
	@Override
	public <T> T findOne(Class<T> documentClass, Bson query) {
		MongoCollection<Document> collection = getCollection( documentClass );
		Document document = findOne(collection, query);
		if (document == null) {
			throw new DocumentNotFoundException(String.format( "Document of type: %s was not found: %s", documentClass.getSimpleName(), bsonToString(query) ) );
		}
		T object = toObject(documentClass, document);
		return object;
	}
	
	@Override
	public <T> Set<T> find(Class<T> documentClass, Bson query) {
		MongoCollection<Document> collection = getCollection( documentClass );
		Set<T> list = new HashSet<>();
		Set<Document> documents = find( collection, query );
		documents.forEach(document -> {
			T object = toObject(documentClass, document);
			list.add(object);
		});
		return list;
	}
	
	
	@Override
	public <T> void refresh(T document) {
		Object id = resolveId(document);
		Bson query = Filters.eq ( "_id", id );
		MongoCollection<Document> collection = getCollection( document.getClass() );
		Document bson = findOne(collection, query);
		if (bson == null) {
			throw new DocumentNotFoundException(String.format( "Document of type: %s was not found: %s", document.getClass().getSimpleName(), bsonToString(query) ) );
		}
		toObject(document, bson);	
	}

	@Override
	public <T> void insertOne(T document) {
		MongoCollection<Document> collection = getCollection( document.getClass() );
		Document bson = toBsonDocument(document);
		insertOne(collection, bson);
		setIdValue(document, bson.get(ID));
		refresh(document);
	}
	
	@Override
	public <T> void upsert(Bson query, T document) {
		MongoCollection<Document> collection = getCollection( document.getClass() );
		Document bson = toBsonDocument(document);
		upsert(collection, bson, query);
		setIdValue(document, bson.get(ID));
		refresh(document);
	}
	
	@Override
	public <T> void replaceOne(T document) {
		Object id = resolveId(document);
		MongoCollection<Document> collection = getCollection( document.getClass() );
		Document bson = toBsonDocument(document);
		replaceOne( collection, bson, Filters.eq ( ID, id ) );
		refresh(document);
	}
	
	@Override
	public <T> void deleteOne(T document) {
		Object id = resolveId(document);
		MongoCollection<Document> collection = getCollection( document.getClass() );
		deleteOne(  collection, Filters.eq ( ID, id ) );
	}
	
	@Override
	public <T> void deleteMany(Class<T> documentClass, Bson query) {
		MongoCollection<Document> collection = getCollection( documentClass );
		deleteMany( collection, query );
	}
	
	@Override
	public <T> T getReference(Class<T> documentClass, Object id) {
		T object = instantiate(documentClass);
		setIdValue(object, id);
		return object;
	}
}