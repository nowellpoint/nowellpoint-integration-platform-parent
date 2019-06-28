package com.nowellpoint.listener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
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
import com.mongodb.ErrorCategory;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.listener.connection.MongoConnection;
import com.nowellpoint.listener.model.ChangeEvent;
import com.nowellpoint.listener.model.ChangeEventHeader;
import com.nowellpoint.listener.model.Event;
import com.nowellpoint.listener.model.Payload;

public class QueueInitalizer {
	
	private static final Logger LOGGER = Logger.getLogger(QueueInitalizer.class);
	private static final String QUEUE = "change.event.listener.queue";
	private static final String CHANGE_EVENTS = "change.events";
	
	private SQSConnectionFactory connectionFactory;
	private SQSConnection connection;
	private Session session;
	
	@Inject
	private MongoDatabase mongoDatabase;
	
	public void onStartup(@Observes @Initialized(value = ApplicationScoped.class) ServletContext context) {
		try {
    		connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(), AmazonSQSClientBuilder.defaultClient());
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            String queueUrl = System.getProperty(QUEUE);
            
            Queue queue = session.createQueue(queueUrl.substring(queueUrl.lastIndexOf("/") + 1));
            MessageConsumer messageConsumer = session.createConsumer(queue);
            
            messageConsumer.setMessageListener(new MessageListener() {
				
				@Override
				public void onMessage(Message message) {
					TextMessage textMessage = (TextMessage) message;
					try {
						
						String token = textMessage.getStringProperty("Token");
						ChangeEvent changeEvent = processChangeEvent(textMessage.getText());
						
						message.acknowledge();
						
					} catch (JMSException e) {
						LOGGER.error(e);
					} catch (MongoWriteException e) {
						if (e.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
			            	LOGGER.warn(e.getMessage());
			            } else {
			            	LOGGER.error(e);
			            }
					} 
				}
			});
			
			connection.start();
				 				
		} catch (JMSException e) {
			LOGGER.error(e);
		} 
	}
	
	public void onShutdown(@Observes @Destroyed(value = ApplicationScoped.class) ServletContext context) {
		try {
			session.close();
			connection.stop();
		} catch (JMSException e) {
			LOGGER.error(e);
		}
	}
	
	private ChangeEvent processChangeEvent(String message) {
		
		JsonbConfig config = new JsonbConfig()
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
		
		LOGGER.info(message);
		
		ChangeEvent changeEvent = jsonb.fromJson(message, ChangeEvent.class);
		
		LOGGER.info(jsonb.toJson(changeEvent));
		
		insert(changeEvent);
		
		return changeEvent;
	}
	
	private void insert(ChangeEvent changeEvent) {
		mongoDatabase.getCollection(CHANGE_EVENTS, ChangeEvent.class).insertOne(changeEvent);
	}
}