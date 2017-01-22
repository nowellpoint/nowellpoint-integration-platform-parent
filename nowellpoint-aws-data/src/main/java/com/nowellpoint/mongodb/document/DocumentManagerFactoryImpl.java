package com.nowellpoint.mongodb.document;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromCodecs;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.List;

import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.jboss.logging.Logger;

import com.mongodb.ConnectionString;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.nowellpoint.mongodb.DocumentManager;
import com.nowellpoint.mongodb.DocumentManagerFactory;

public class DocumentManagerFactoryImpl implements DocumentManagerFactory {
	
	private static final Logger LOGGER = Logger.getLogger(DocumentManagerFactory.class);
	
	private static CollectionNameResolver collectionNameResolver = new CollectionNameResolver();
	
	private static MongoClient client;
	private static MongoDatabase database;
	
	public DocumentManagerFactoryImpl(ConnectionString connectionString) {
		client = MongoClients.create(connectionString);
		database = client.getDatabase(connectionString.getDatabase());
		LOGGER.info("***Connected to: " + database.getName());
	}
	
	public DocumentManagerFactoryImpl(ConnectionString connectionString, List<Codec<?>> codecs) {
		CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(), fromCodecs(codecs));
		client = MongoClients.create(connectionString);
		database = client.getDatabase(connectionString.getDatabase()).withCodecRegistry(codecRegistry);
		LOGGER.info("***Connected to: " + database.getName());
	}
	
	@Override
	public DocumentManager createDocumentManager() {
		return new DocumentManagerImpl(this);
	}
	
	@Override
	public <T> MongoCollection<T> getCollection(Class<T> documentClass) {
		return (MongoCollection<T>) database.getCollection(resolveCollectionName(documentClass), documentClass);
	}
	
	@Override
	public <T> String resolveCollectionName(Class<T> documentClass) {
		return collectionNameResolver.resolveCollectionName(documentClass);
	}
	
	@Override
	public void close() {
		client.close();
		LOGGER.info("***Closed connection to: " + database.getName());
	}
	
	@Override
	public String bsonToString(Bson bson) {
		return bson.toBsonDocument(Document.class, getDefaultCodecRegistry()).toString();
	}

	@Override
	public MongoDatabase getDatabase() {
		return database;
	}
}