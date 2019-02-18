package com.nowellpoint.sqs.spi;

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

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

public class SQSBootstrapExtension implements Extension {
	
	private static final Logger LOGGER = Logger.getLogger(SQSBootstrapExtension.class);
	
	private static SQSConnectionFactory connectionFactory;
	
	private static SQSConnection connection;
	
	private static Session session;
	
	public <T> void processAnnotatedType(@Observes @WithAnnotations({QueueListener.class}) ProcessAnnotatedType<T> type, BeanManager beanManager) {
		
    	if (type.getAnnotatedType().getJavaClass().isAnnotationPresent(QueueListener.class)) {
    		
    		QueueListener queueListener = type.getAnnotatedType().getJavaClass().getAnnotation(QueueListener.class);
    		
    		LOGGER.info(String.format("setting up queue: %s", queueListener.queueName()));

			try {
				Queue queue = session.createQueue(queueListener.queueName());
				
				MessageConsumer consumer = session.createConsumer(queue);
				
				MessageListener listener = (MessageListener) Class.forName(type.getAnnotatedType().getJavaClass().getName()).newInstance();
				 
				consumer.setMessageListener(listener);
				
			} catch (JMSException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				LOGGER.error(e);
			}
    	}
    } 
	
	public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery event) {
		
    	LOGGER.info("beginning the simple queue scanning process");
    	
    	connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(),AmazonSQSClientBuilder.defaultClient());
    	
    	try {
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
		
		LOGGER.info("starting queue connections");
		
		try {
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