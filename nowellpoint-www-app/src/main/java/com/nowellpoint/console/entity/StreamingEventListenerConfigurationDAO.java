package com.nowellpoint.console.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class StreamingEventListenerConfigurationDAO extends BasicDAO<StreamingEventListenerConfiguration,ObjectId> {
	
	public StreamingEventListenerConfigurationDAO(Class<StreamingEventListenerConfiguration> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
}