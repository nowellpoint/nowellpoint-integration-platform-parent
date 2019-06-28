package com.nowellpoint.listener.service;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.jboss.logging.Logger;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.util.SecretsManager;

@ApplicationScoped
public class DatabaseProducer {
	
	private static final Logger logger = Logger.getLogger(DatabaseProducer.class);
	private static final MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", SecretsManager.getMongoClientUri()), new MongoClientOptions.Builder());
	
	@Produces
    public MongoClient createClient() {
    	return new MongoClient(mongoClientUri);
	}

	@Produces
	public MongoDatabase createDatabase(MongoClient mongoClient) {
		
		CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		
		MongoDatabase mongoDatabase = null;
    	
    	try {
			mongoDatabase = mongoClient.getDatabase(mongoClientUri.getDatabase()).withCodecRegistry(codecRegistry);
    	} catch (MongoException e) {
    		logger.error("An error occoured when connecting to MongoDB", e);
    	} catch (Exception e) {
    		logger.error("An error occoured when connecting to MongoDB", e);
    	}
    	
    	return mongoDatabase;
	}
	
	public void close(@Disposes MongoClient mongoClient) {
		mongoClient.close();
    }
}