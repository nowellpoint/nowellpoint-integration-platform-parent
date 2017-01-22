package com.nowellpoint.mongodb;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.async.client.FindIterable;

public interface DocumentManager {
	
	<T> String resolveCollectionName(Class<T> documentClass);
	
	<T> T findOne(Class<T> documentClass, ObjectId id);
	
	<T> T findOne(Class<T> documentClass, Bson query);
	
	<T> FindIterable<T> find(Class<T> documentClass, Bson query);
	
	<T> void insertOne(T document);
	
	<T> void replaceOne(T document);
	
	<T> void deleteOne(T document);
	
	<T> void deleteMany(Class<T> documentClass, Bson query);

}