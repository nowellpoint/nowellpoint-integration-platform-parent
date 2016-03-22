package com.nowellpoint.aws.api.bus.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;

import org.bson.codecs.Codec;

import com.nowellpoint.aws.api.bus.MongoDBQueueListener;
import com.nowellpoint.aws.data.MongoDBDatastore;
import com.nowellpoint.aws.data.annotation.Document;
import com.nowellpoint.aws.data.annotation.MessageHandler;

public class MongoDBBusExtension implements Extension {
	
	/**
     * 
     */
    
    private static final Logger LOGGER = Logger.getLogger(MongoDBBusExtension.class.getName());
    
    private List<Codec<?>> codecs = new ArrayList<Codec<?>>();
    private Map<String,Class<?>> queueMap = new HashMap<String,Class<?>>();
    
    public <T> void processAnnotatedType(@Observes @WithAnnotations({Document.class}) ProcessAnnotatedType<T> type, BeanManager beanManager) {
    	if (type.getAnnotatedType().getJavaClass().isAnnotationPresent(MessageHandler.class)) {    
    		LOGGER.info("processAnnotatedType: " + type.getAnnotatedType().getJavaClass().getName());
    		try {
    			Document document = type.getAnnotatedType().getJavaClass().getAnnotation(Document.class);
    			codecs.add((Codec<?>) document.codec().newInstance());
    			
    			if (type.getAnnotatedType().getJavaClass().isAnnotationPresent(MessageHandler.class)) {
    				MessageHandler messageHandler = type.getAnnotatedType().getJavaClass().getAnnotation(MessageHandler.class);
    				queueMap.put(messageHandler.queueName(), messageHandler.messageListener());
    			}
			} catch (InstantiationException | IllegalAccessException e) {
				LOGGER.error(e);
			}
    	}
    } 
    
    public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery discovery) {
    	LOGGER.info("beginning the scanning process");
    }
    
    public void afterBeanDiscovery(@Observes AfterBeanDiscovery discovery) {
    	MongoDBDatastore.registerCodecs(codecs);
    	MongoDBQueueListener.registerQueues(queueMap);
    	LOGGER.info("finished the scanning process");
    }
}