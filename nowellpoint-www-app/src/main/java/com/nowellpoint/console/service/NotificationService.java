package com.nowellpoint.console.service;

import java.util.List;

import com.nowellpoint.console.model.Notification;

public interface NotificationService {
	public List<Notification> getNotifications(String organizationId);
}