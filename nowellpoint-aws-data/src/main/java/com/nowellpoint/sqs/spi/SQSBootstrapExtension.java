package com.nowellpoint.sqs.spi;

import java.util.HashMap;
import java.util.Map;

import com.nowellpoint.aws.data.QueueListener;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;

import org.jboss.logging.Logger;

import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;

public class SQSBootstrapExtension implements Extension {
	
	private static final Logger LOGGER = Logger.getLogger(SQSBootstrapExtension.class);
	
	private static SQSConnectionFactory connectionFactory;
	
	private static SQSConnection connection;
	
	private static Session session;
	
	private Map<String,String> queueMap = new HashMap<String,String>();
	
	public <T> void processAnnotatedType(@Observes @WithAnnotations({QueueListener.class}) ProcessAnnotatedType<T> type, BeanManager beanManager) {
    	if (type.getAnnotatedType().getJavaClass().isAnnotationPresent(QueueListener.class)) {
    		QueueListener queueListener = type.getAnnotatedType().getJavaClass().getAnnotation(QueueListener.class);
			//queueMap.put(queue.queueName(), type.getAnnotatedType().getJavaClass().getName());
    		LOGGER.info(String.format("setting up queue: %s", queueListener.queueName()));

			try {
				Queue queue = session.createQueue(queueListener.queueName());
				
				MessageConsumer consumer = session.createConsumer(queue);
				
				MessageListener listener = (MessageListener) Class.forName(queueMap.get(queueListener.queueName())).newInstance();
				 
				consumer.setMessageListener(listener);
				
			} catch (JMSException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				LOGGER.error(e);
			}
    	}
    } 
	
	public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery event) {
    	LOGGER.info("beginning the simple queue scanning process");
    	
    	try {
    		connectionFactory = SQSConnectionFactory.builder().build();	
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
		} catch (JMSException e) {
			LOGGER.error(e);
		}
    }
	
	public void afterBeanDiscovery(@Observes AfterBeanDiscovery event) {
		LOGGER.info("finished the simple queue scanning process");
	}
	
	public void afterDeploymentValidation(@Observes AfterDeploymentValidation event) {
		
//		if (queueMap.isEmpty()) {
//			return;
//		}
//		
//		SQSConnectionFactory connectionFactory = SQSConnectionFactory.builder().build();	
//		 
//		try {
//			connection = connectionFactory.createConnection();
//			
//			Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
//			
//			for (String name : queueMap.keySet()) {
//				
//				LOGGER.info(String.format("setting up queue: %s", name));
//				
//				Queue queue =  session.createQueue(name);
//				
//				MessageConsumer consumer = session.createConsumer(queue);
//				
//				LOGGER.info(queueMap.get(name));
//				
//				MessageListener listener = (MessageListener) Class.forName(queueMap.get(name)).newInstance();
//				 
//				consumer.setMessageListener(listener);
//			}
//			
//		} catch (JMSException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
//			LOGGER.error(e);
//		}
		
		try {
			Thread.sleep(10000);
			connection.start();
			Thread.sleep(1000);
		} catch (JMSException | InterruptedException e) {
			LOGGER.error(e);
		}
	}
	
	
	public void beforeShutdown(@Observes BeforeShutdown event) {
		
		LOGGER.info("shutting down queue listeners");
		
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