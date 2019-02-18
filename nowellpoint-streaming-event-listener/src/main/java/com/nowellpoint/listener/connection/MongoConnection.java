package com.nowellpoint.listener.connection;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.nowellpoint.listener.model.StreamingEvent;
import com.nowellpoint.util.SecretsManager;

public class MongoConnection {

	private static MongoConnection INSTANCE = new MongoConnection();
	
	private MongoClient mongoClient;
	private Datastore datastore;

	private MongoConnection() {}
	
	public void connect() {
		String clientUri = String.format("mongodb://%s", SecretsManager.getMongoClientUri());
    	MongoClientOptions.Builder options = new MongoClientOptions.Builder();
    	
    	MongoClientURI mongoClientUri = new MongoClientURI(clientUri, options);
		mongoClient = new MongoClient(mongoClientUri);
        
        final Morphia morphia = new Morphia();
        morphia.map(StreamingEvent.class);

        datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
        datastore.ensureIndexes();
	}
	
	public void disconnect() {
		mongoClient.close();
	}
	
	public Datastore getDatastore() {
		return datastore;
	}
	
	public static MongoConnection getInstance() {
		return INSTANCE;
	}
}