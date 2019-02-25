package com.nowellpoint.console.service.impl;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;

import com.nowellpoint.console.model.Notification;
import com.nowellpoint.console.model.Notifications;
import com.nowellpoint.console.entity.NotificationDAO;
import com.nowellpoint.console.service.AbstractService;
import com.nowellpoint.console.service.NotificationService;

public class NotificationServiceImpl extends AbstractService implements NotificationService {
	
	private NotificationDAO dao;

	public NotificationServiceImpl() {
		dao = new NotificationDAO(com.nowellpoint.console.entity.Notification.class, datastore);
	}
	
	@Override
	public List<Notification> getNotifications(String organizationId) {
		Query<com.nowellpoint.console.entity.Notification> query = dao.createQuery()
				.field("organizationId")
				.equal(new ObjectId(organizationId))
				.order("-receivedOn");
		
		QueryResults<com.nowellpoint.console.entity.Notification> results = dao.find(query);
		
		FindOptions options = new FindOptions().batchSize(50);
		
		return Notifications.of(results.asList(options));
	}
}