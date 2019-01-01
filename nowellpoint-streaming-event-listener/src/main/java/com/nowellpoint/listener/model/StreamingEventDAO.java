package com.nowellpoint.listener.model;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class StreamingEventDAO extends BasicDAO<StreamingEvent,String> {
	
	public StreamingEventDAO(Class<StreamingEvent> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
}