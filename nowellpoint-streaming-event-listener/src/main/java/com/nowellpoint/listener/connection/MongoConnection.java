package com.nowellpoint.listener.connection;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.util.SecretsManager;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.jboss.logging.Logger;

public class MongoConnection {

	private static Logger logger = Logger.getLogger(MongoConnection.class);
	private static MongoConnection instance = new MongoConnection();
	
	private MongoClient mongoClient;
	private MongoDatabase mongoDatabase;

	private MongoConnection() {}
	
	public void connect() {
		if (mongoClient == null) {
			String clientUri = String.format("mongodb://%s", SecretsManager.getMongoClientUri());
			
	    	MongoClientOptions.Builder options = new MongoClientOptions.Builder();
	    	
	    	MongoClientURI mongoClientUri = new MongoClientURI(clientUri, options);
	    	
	    	CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
	                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
	    	
	    	try {
	    		mongoClient = new MongoClient(mongoClientUri);
				mongoDatabase = mongoClient.getDatabase(mongoClientUri.getDatabase()).withCodecRegistry(codecRegistry);
	    	} catch (MongoException e) {
	    		logger.error("An error occoured when connecting to MongoDB", e);
	    	} catch (Exception e) {
	    		logger.error("An error occoured when connecting to MongoDB", e);
	    	}
		}
	}
	
	public void disconnect() {
		mongoClient.close();
	}
	
	public MongoDatabase getDatabase() {
		return mongoDatabase;
	}
	
	public static MongoConnection getInstance() {
		return instance;
	}
}