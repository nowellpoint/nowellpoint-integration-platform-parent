package com.nowellpoint.listener.service.impl;

import javax.inject.Inject;

import org.jboss.logging.Logger;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.listener.model.ChangeEvent;
import com.nowellpoint.listener.service.ChangeEventService;

public class ChangeEventServiceImpl implements ChangeEventService {
	
	private static final String CHANGE_EVENTS = "change.events";
	
	@Inject
	private Logger logger;
	
	@Inject
	private MongoDatabase mongoDatabase;

	@Override
	public void create(ChangeEvent changeEvent) {
		try {
			mongoDatabase.getCollection(CHANGE_EVENTS, ChangeEvent.class).insertOne(changeEvent);
		} catch (MongoWriteException e) {
			if (e.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
				logger.warn(e.getMessage());
            } else {
            	throw e;
            }
		} 
	}
}