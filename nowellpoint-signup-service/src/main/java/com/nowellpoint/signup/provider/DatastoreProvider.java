package com.nowellpoint.signup.provider;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.signup.entity.RegistrationDocument;
import com.nowellpoint.util.EnvironmentVariables;

@ApplicationScoped
public class DatastoreProvider {
	
	private Datastore datastore;
	
	public Datastore getDatastore() {
		return datastore;
	}
	
	@PostConstruct
	public void init() {
		final MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", System.getenv(EnvironmentVariables.MONGO_CLIENT_URI)));
        final MongoClient mongoClient = new MongoClient(mongoClientUri);
        final Morphia morphia = new Morphia().map(RegistrationDocument.class);

        datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
        datastore.ensureIndexes();
	}
}