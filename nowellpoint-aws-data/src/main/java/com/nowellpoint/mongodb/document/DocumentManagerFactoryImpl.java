package com.nowellpoint.mongodb.document;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromCodecs;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.List;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.jboss.logging.Logger;

import com.mongodb.ConnectionString;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;
import com.nowellpoint.mongodb.DocumentManagerFactory;

public class DocumentManagerFactoryImpl implements DocumentManagerFactory {
	
	private static final Logger LOGGER = Logger.getLogger(DocumentManagerFactory.class);
	
	private static MongoClient client;
	private static MongoDatabase database;
	
	public DocumentManagerFactoryImpl(String mongoUri, List<Codec<?>> codecs) {
		CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(), fromCodecs(codecs));
		ConnectionString connectionString = new ConnectionString(mongoUri);
		client = MongoClients.create(connectionString);
		database = client.getDatabase(connectionString.getDatabase()).withCodecRegistry(codecRegistry);
		LOGGER.info("***Connected to: " + database.getName());
	}
	
	@Override
	public void close() {
		client.close();
		LOGGER.info("***Closed connection to: " + database.getName());
	}

	@Override
	public MongoDatabase getDatabase() {
		return database;
	}
}