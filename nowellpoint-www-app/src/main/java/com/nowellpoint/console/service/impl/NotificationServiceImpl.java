package com.nowellpoint.console.service.impl;

import java.util.List;

import org.bson.types.ObjectId;

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
		return Notifications.of(dao.getNotifications(new ObjectId(organizationId)));
	}
}