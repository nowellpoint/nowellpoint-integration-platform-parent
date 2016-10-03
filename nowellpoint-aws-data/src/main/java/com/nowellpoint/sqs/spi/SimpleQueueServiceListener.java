package com.nowellpoint.sqs.spi;

import java.util.HashMap;
import java.util.Map;

import com.nowellpoint.aws.data.SimpleQueueListener;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
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
public class SimpleQueueServiceListener implements Extension, ServletContextListener {
	
	private static final Logger LOGGER = Logger.getLogger(SimpleQueueServiceListener.class);
	
	private static SQSConnection connection;
	
	private Map<String,Class<?>> queueMap = new HashMap<String,Class<?>>();
	
	public <T> void processAnnotatedType(@Observes @WithAnnotations({SimpleQueueListener.class}) ProcessAnnotatedType<T> type, BeanManager beanManager) {
    	if (type.getAnnotatedType().getJavaClass().isAnnotationPresent(SimpleQueueListener.class)) {    
    		LOGGER.info("processAnnotatedType: " + type.getAnnotatedType().getJavaClass().getName());
    		SimpleQueueListener queue = type.getAnnotatedType().getJavaClass().getAnnotation(SimpleQueueListener.class);
			queueMap.put(queue.queueName(), type.getAnnotatedType().getJavaClass());
    	}
    } 
	
	public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery discovery) {
    	LOGGER.info("beginning the simple queue scanning process");
    }
	
	public void afterBeanDiscovery(@Observes AfterBeanDiscovery discovery) {
		LOGGER.info("finished the simple queue scanning process");
	}
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		
		if (queueMap.isEmpty()) {
			return;
		}
		
		SQSConnectionFactory connectionFactory = SQSConnectionFactory.builder().build();	
		 
		try {
			connection = connectionFactory.createConnection();
			
			Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			
			for (String name : queueMap.keySet()) {
				
				Queue queue =  session.createQueue(name);
				
				MessageConsumer consumer = session.createConsumer(queue);
				
				MessageListener listener = (MessageListener) queueMap.get(name).newInstance();
				 
				consumer.setMessageListener(listener);
			}
			
		} catch (JMSException | InstantiationException | IllegalAccessException e) {
			LOGGER.error(e);
		}
		
		try {
			connection.start();
			Thread.sleep(1000);
		} catch (JMSException | InterruptedException e) {
			LOGGER.error(e);
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
		if (connection == null) {
			return;
		}
		
		try {
			connection.close();
		} catch (JMSException e) {
			LOGGER.error(e);
		}
	}
}