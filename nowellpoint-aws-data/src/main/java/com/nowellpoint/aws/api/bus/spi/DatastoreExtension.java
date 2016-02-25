package com.nowellpoint.aws.api.bus.spi;

import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import com.nowellpoint.aws.data.annotation.Handler;

public class DatastoreExtension implements Extension {
	
	/**
     * 
     */
    
    private static final Logger LOG = Logger.getLogger(DatastoreExtension.class.getName());
    
    <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat, BeanManager beanManager) {
    	if (pat.getAnnotatedType().getJavaClass().isAnnotationPresent(Handler.class)) {    
    		LOG.info("processAnnotatedType: " + pat.getAnnotatedType().getJavaClass().getName());
    	}
    } 
}