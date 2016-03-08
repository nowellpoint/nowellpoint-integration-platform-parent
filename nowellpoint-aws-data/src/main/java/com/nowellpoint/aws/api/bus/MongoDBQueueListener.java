package com.nowellpoint.aws.api.bus;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.jboss.logging.Logger;

import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;

@WebListener
public class MongoDBQueueListener implements ServletContextListener {
	
	private static final Logger LOGGER = Logger.getLogger(MongoDBQueueListener.class);
	
	private static SQSConnection connection;
	
	public static void registerQueues(Map<String,Class<?>> queueMap) {
		
		SQSConnectionFactory connectionFactory = SQSConnectionFactory.builder().build();	
		 
		try {
			connection = connectionFactory.createConnection();
			
			Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			
			for (String name : queueMap.keySet()) {
				
				Queue queue =  session.createQueue(name);
				
				MessageConsumer consumer = session.createConsumer(queue);
				
				MessageListener listner = (MessageListener) queueMap.get(name).newInstance();
				 
				consumer.setMessageListener(listner);
			}
			
		} catch (JMSException | InstantiationException | IllegalAccessException e) {
			LOGGER.error(e);
		}
	}
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			connection.start();
			Thread.sleep(1000);
		} catch (JMSException | InterruptedException e) {
			LOGGER.error(e);
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		try {
			connection.close();
		} catch (JMSException e) {
			LOGGER.error(e);
		}
	}
}