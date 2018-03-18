package com.nowellpoint.signup.provider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.signup.entity.RegistrationDocument;
import com.nowellpoint.signup.entity.UserProfile;
import com.nowellpoint.util.EnvironmentVariables;

@ApplicationScoped
public class DatastoreProvider {
	
	private Datastore datastore;
	private MongoClient mongoClient;
	
	public Datastore getDatastore() {
		return datastore;
	}
	
	public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
		final MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", System.getenv(EnvironmentVariables.MONGO_CLIENT_URI)));
        mongoClient = new MongoClient(mongoClientUri);
        
        final Morphia morphia = new Morphia();
        
        morphia.map(RegistrationDocument.class);
        morphia.map(UserProfile.class);

        datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
        datastore.ensureIndexes();
	}
	
	public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object destroy) {
		mongoClient.close();
	}
}