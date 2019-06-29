package com.nowellpoint.listener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;
import javax.servlet.ServletContext;

import org.jboss.logging.Logger;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.mongodb.MongoWriteException;
import com.nowellpoint.listener.connection.CacheManager;
import com.nowellpoint.listener.model.ChangeEvent;
import com.nowellpoint.listener.service.ChangeEventService;

@ApplicationScoped
public class ChangeEventListener {
	
	private static final String QUEUE = "change.event.listener.queue";
	
	private SQSConnectionFactory connectionFactory;
	private SQSConnection connection;
	private Session session;
	
	@Inject
	private Logger logger;
	
	@Inject
	private ChangeEventService changeEventService;
	
	@Inject
    private Event<ChangeEvent> event;
	
	public void start(@Observes @Initialized(value = ApplicationScoped.class) ServletContext context) {
		try {
    		connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(), AmazonSQSClientBuilder.defaultClient());
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            String queueUrl = System.getProperty(QUEUE);
            
            Queue queue = session.createQueue(queueUrl.substring(queueUrl.lastIndexOf("/") + 1));
            MessageConsumer messageConsumer = session.createConsumer(queue);
            
            messageConsumer.setMessageListener(new MessageListener() {
            	
            	final JsonbConfig config = new JsonbConfig()
        				.withNullValues(Boolean.TRUE)
        				.withPropertyVisibilityStrategy(
        						new PropertyVisibilityStrategy() {
        							@Override
        							public boolean isVisible(Field field) {
        								return true;
        							}
        							
        							@Override
        							public boolean isVisible(Method method) {
        								return false;
        							}
        						});
        		
        		final Jsonb jsonb = JsonbBuilder.create(config);
				
				@Override
				public void onMessage(Message message) {
					TextMessage textMessage = (TextMessage) message;
					try {
						
						String token = textMessage.getStringProperty("Token");
						
						ChangeEvent changeEvent = jsonb.fromJson(textMessage.getText(), ChangeEvent.class);
						
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
			});
			
			connection.start();
				 				
		} catch (JMSException e) {
			logger.error(e);
		} 
	}
	
	public void stop(@Observes @Destroyed(value = ApplicationScoped.class) ServletContext context) {
		try {
			session.close();
			connection.stop();
		} catch (JMSException e) {
			logger.error(e);
		}
	}
}