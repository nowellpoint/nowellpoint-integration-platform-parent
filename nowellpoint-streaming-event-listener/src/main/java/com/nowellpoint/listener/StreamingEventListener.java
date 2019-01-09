package com.nowellpoint.listener;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.QueryResults;

import com.nowellpoint.listener.model.StreamingEventListenerConfiguration;

public class StreamingEventListener {

	private Datastore datastore;
	
	public StreamingEventListener(Datastore datastore) {
		this.datastore = datastore;
	}
	
	public void start() {
		QueryResults<StreamingEventListenerConfiguration> queryResults = datastore.find(StreamingEventListenerConfiguration.class);
        queryResults.asList().stream().forEach(configuration -> {
        	new TopicSubscription(datastore, configuration);
        });
	}
	
	public void stop() {
		
	}
}