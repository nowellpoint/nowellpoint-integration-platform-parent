package com.nowellpoint.listener.connection;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.util.SecretsManager;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class MongoConnection {

	private static MongoConnection INSTANCE = new MongoConnection();
	
	private MongoClient mongoClient;
	private MongoDatabase mongoDatabase;

	private MongoConnection() {}
	
	public void connect() {
		String clientUri = String.format("mongodb://%s", SecretsManager.getMongoClientUri());
    	MongoClientOptions.Builder options = new MongoClientOptions.Builder();
    	
    	MongoClientURI mongoClientUri = new MongoClientURI(clientUri, options);
		mongoClient = new MongoClient(mongoClientUri);
		
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		
		mongoDatabase = mongoClient.getDatabase(mongoClientUri.getDatabase()).withCodecRegistry(pojoCodecRegistry);
	}
	
	public void disconnect() {
		mongoClient.close();
	}
	
	public MongoDatabase getMongoDatabase() {
		return mongoDatabase;
	}
	
	public static MongoConnection getInstance() {
		return INSTANCE;
	}
}