package com.nowellpoint.console.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.FindOptions;

public class NotificationDAO extends BasicDAO<Notification, ObjectId>{

	public NotificationDAO(Class<Notification> entityClass, Datastore ds) {
		super(entityClass, ds);
	}
	
	public List<Notification> getNotifications(ObjectId organizationId) {
		
		FindOptions options = new FindOptions().limit(50);
		
		List<Notification> notifications = getDatastore().createQuery(Notification.class)
				.field("organizationId")
				.equal(organizationId)
				.order("-receivedOn")
				.asList(options);
		
		return notifications;
	}
}