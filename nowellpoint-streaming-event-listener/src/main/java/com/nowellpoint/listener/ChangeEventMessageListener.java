package com.nowellpoint.listener;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.jboss.logging.Logger;

import com.mongodb.MongoWriteException;
import com.nowellpoint.listener.model.ChangeEvent;
import com.nowellpoint.listener.service.ChangeEventService;
import com.nowellpoint.listener.util.CacheManager;
import com.nowellpoint.listener.util.JsonbUtil;

public class ChangeEventMessageListener implements MessageListener {

	@Inject
	private Logger logger;
	
	@Inject
	private ChangeEventService changeEventService;
	
	@Inject
    private Event<ChangeEvent> event;
	
	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage) message;
		try {
			
			String token = textMessage.getStringProperty("Token");
			
			ChangeEvent changeEvent = JsonbUtil.getJsonb().fromJson(textMessage.getText(), ChangeEvent.class);
			
			CacheManager.put(changeEvent.getOrganizationId(), token);
			
			event.fire(changeEvent);
			
			changeEvent.getPayload().getChangeEventHeader().setProcessTimestamp(System.currentTimeMillis());
			
			changeEventService.create(changeEvent);
			
		} catch (JMSException | MongoWriteException e) {
			logger.error(e);
		} finally {
			try {
				message.acknowledge();
			} catch (JMSException e) {
				logger.error(e);
			}
		}
	}
}