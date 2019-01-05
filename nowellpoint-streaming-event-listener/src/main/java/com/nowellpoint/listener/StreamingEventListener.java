package com.nowellpoint.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.QueryResults;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.listener.model.StreamingEvent;
import com.nowellpoint.listener.model.StreamingEventListenerConfiguration;
import com.nowellpoint.listener.model.StreamingEventReplayId;
import com.nowellpoint.util.SecretsManager;

@WebListener
public class StreamingEventListener implements ServletContextListener {
    
    private MongoClient mongoClient;
	private Datastore datastore;
    
    @Override
	public void contextInitialized(ServletContextEvent event) {
    	
    	MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", SecretsManager.getMongoClientUri()));
		mongoClient = new MongoClient(mongoClientUri);
        
        final Morphia morphia = new Morphia();
        morphia.map(StreamingEvent.class);
        morphia.map(StreamingEventReplayId.class);
        morphia.map(StreamingEventListenerConfiguration.class);

        datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
        datastore.ensureIndexes();
        
        QueryResults<StreamingEventListenerConfiguration> queryResults = datastore.find(StreamingEventListenerConfiguration.class).filter("active =", Boolean.TRUE);
        
        queryResults.asList().stream().forEach(c -> {
        	new StreamingEventListenerSubscription(datastore, c);
        });
    }
    
    @Override
	public void contextDestroyed(ServletContextEvent event) {
    	mongoClient.close();
    }
}