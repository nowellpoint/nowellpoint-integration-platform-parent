package com.nowellpoint.console.service;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.www.app.util.EnvironmentVariables;

public abstract class AbstractService {
	
	protected static final Datastore datastore;
	
	static {
		final MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", EnvironmentVariables.getMongoClientUri()));
		MongoClient mongoClient = new MongoClient(mongoClientUri);
        
        final Morphia morphia = new Morphia();
        
        //morphia.map(UserProfileEntity.class);

        datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
        datastore.ensureIndexes();
	}
}