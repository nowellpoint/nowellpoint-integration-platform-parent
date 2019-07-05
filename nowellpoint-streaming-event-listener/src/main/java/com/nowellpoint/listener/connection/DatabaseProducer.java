package com.nowellpoint.listener.connection;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

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

public class DatabaseProducer {

	private static final MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", SecretsManager.getMongoClientUri()), new MongoClientOptions.Builder());
	
	@Inject
	private Logger logger;
	
	@Produces
	@ApplicationScoped
    public MongoClient createClient() {
		System.out.println("@produces createClient()" + mongoClientUri.getHosts().get(0));
    	return new MongoClient(mongoClientUri);
	}

	@Produces
	@ApplicationScoped
	public MongoDatabase createDatabase(MongoClient mongoClient) {
		
		System.out.println("@produces createDatabase" + mongoClientUri.getDatabase());
		
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