package com.nowellpoint.mongodb;

import java.util.Set;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

public interface DocumentManager {
	
	<T> String resolveCollectionName(Class<T> documentClass);
	
	<T> Set<T> findAll(Class<T> documentClass);
	
	<T> T fetch(Class<T> documentClass, ObjectId id);
	
	<T> T findOne(Class<T> documentClass, Bson query);
	
	<T> Set<T> find(Class<T> documentClass, Bson query);
	
	<T> void refresh(T document);
	
	<T> void insertOne(T document);
	
	<T> void replaceOne(T document);
	
	<T> void deleteOne(T document);
	
	<T> void deleteMany(Class<T> documentClass, Bson query);
	
	<T> void upsert(Bson query, T document);
	
	<T> T getReference(Class<T> documentClass, Object id);

}