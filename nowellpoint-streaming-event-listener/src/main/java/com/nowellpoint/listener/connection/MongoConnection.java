package com.nowellpoint.listener.connection;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.util.SecretsManager;

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
		
		mongoDatabase = mongoClient.getDatabase(mongoClientUri.getDatabase());
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