package com.nowellpoint.listener.service.impl;

import javax.inject.Inject;

import com.mongodb.client.MongoDatabase;
import com.nowellpoint.listener.model.Notification;
import com.nowellpoint.listener.service.NotificationService;

public class NotificationServiceImpl implements NotificationService {
	
	private static final String COLLECTION = "notifications";

	@Inject
	private MongoDatabase mongoDatabase;
	
	@Override
	public void create(Notification notification) {
		mongoDatabase.getCollection(COLLECTION, Notification.class).insertOne(notification);
	}
}