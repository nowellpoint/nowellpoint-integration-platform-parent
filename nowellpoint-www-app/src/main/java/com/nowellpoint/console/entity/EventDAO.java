package com.nowellpoint.console.entity;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class EventDAO extends BasicDAO<Event,String> {
	
	public EventDAO(Class<Event> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
}