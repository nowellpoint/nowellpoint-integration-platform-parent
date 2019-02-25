package com.nowellpoint.console.entity;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;

public class NotificationDAO extends BasicDAO<Notification, ObjectId>{

	public NotificationDAO(Class<Notification> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
}