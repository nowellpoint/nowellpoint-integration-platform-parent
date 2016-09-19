package com.nowellpoint.mongodb.spi;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;

import org.bson.codecs.Codec;
import org.jboss.logging.Logger;

import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.document.MongoDatastore;

public class MongoBootstrapExtension implements Extension {
	
	/**
     * 
     */
    
    private static final Logger LOGGER = Logger.getLogger(MongoBootstrapExtension.class.getName());
    
    private List<Codec<?>> codecs = new ArrayList<Codec<?>>();
    
    public <T> void processAnnotatedType(@Observes @WithAnnotations({Document.class}) ProcessAnnotatedType<T> type, BeanManager beanManager) {
    	if (type.getAnnotatedType().getJavaClass().isAnnotationPresent(Document.class)) {    
    		LOGGER.info("processAnnotatedType: " + type.getAnnotatedType().getJavaClass().getName());
    		try {
    			Document document = type.getAnnotatedType().getJavaClass().getAnnotation(Document.class);
    			codecs.add((Codec<?>) document.codec().newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				LOGGER.error(e);
			}
    	}
    } 
    
    public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery discovery) {
    	LOGGER.info("beginning the scanning process");
    }
    
    public void afterBeanDiscovery(@Observes AfterBeanDiscovery discovery) {
    	MongoDatastore.registerCodecs(codecs);
    	LOGGER.info("finished the scanning process");
    }
}