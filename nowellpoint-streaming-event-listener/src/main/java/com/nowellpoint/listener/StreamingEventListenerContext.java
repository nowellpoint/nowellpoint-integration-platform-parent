package com.nowellpoint.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.nowellpoint.listener.model.StreamingEvent;
import com.nowellpoint.listener.model.StreamingEventListenerConfiguration;
import com.nowellpoint.listener.model.ReplayId;
import com.nowellpoint.util.SecretsManager;

@WebListener
public class StreamingEventListenerContext implements ServletContextListener {
    
    private MongoClient mongoClient;
    private StreamingEventListener listener;
    
    @Override
	public void contextInitialized(ServletContextEvent event) {
    	
    	String clientUri = String.format("mongodb://%s", SecretsManager.getMongoClientUri());
    	MongoClientOptions.Builder options = new MongoClientOptions.Builder();
    	
    	MongoClientURI mongoClientUri = new MongoClientURI(clientUri, options);
		mongoClient = new MongoClient(mongoClientUri);
        
        final Morphia morphia = new Morphia();
        morphia.map(StreamingEvent.class);
        morphia.map(ReplayId.class);
        morphia.map(StreamingEventListenerConfiguration.class);

        Datastore datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
        datastore.ensureIndexes();
        
        listener = new StreamingEventListener(datastore);
        listener.start();
    }
    
    @Override
	public void contextDestroyed(ServletContextEvent event) {
    	mongoClient.close();
    }
}