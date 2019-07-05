package com.nowellpoint.listener;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jboss.logging.Logger;

import com.mongodb.MongoWriteException;
import com.nowellpoint.listener.model.Notification;
import com.nowellpoint.listener.service.NotificationService;
import com.nowellpoint.listener.util.JsonbUtil;

public class NotificationMessageListener implements MessageListener {
	
	@Inject
	private Logger logger;
	
	@Inject
	private NotificationService notificationService;

	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			Notification notification = JsonbUtil.getJsonb().fromJson(textMessage.getText(), Notification.class);
			notificationService.create(notification);
			message.acknowledge();
		} catch (JMSException | MongoWriteException e) {
			logger.error(e);
		} 
	}
}