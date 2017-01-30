package com.nowellpoint.mongodb;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;

public interface DocumentManagerFactory {
	
	MongoCollection<Document> getCollection(Class<?> documentClass);
	
	MongoDatabase getDatabase();
	
	DocumentManager createDocumentManager();
	
	<T> String resolveCollectionName(Class<T> documentClass);
	
	String bsonToString(Bson bson);
	
	void close();
}