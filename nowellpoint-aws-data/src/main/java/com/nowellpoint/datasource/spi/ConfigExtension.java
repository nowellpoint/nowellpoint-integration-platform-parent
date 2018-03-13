package com.nowellpoint.datasource.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.enterprise.util.AnnotationLiteral;

import org.jboss.logging.Logger;

import com.nowellpoint.mongodb.Datastore;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.annotation.Document;

public class ConfigExtension implements Extension {
	
	private static final Logger LOGGER = Logger.getLogger(ConfigExtension.class);
	
	private Bean<DocumentManagerFactory> bean;
    
    <T> void processAnnotatedType(@Observes @WithAnnotations({Document.class}) ProcessAnnotatedType<T> type, BeanManager beanManager) {
    	if (type.getAnnotatedType().getJavaClass().isAnnotationPresent(Document.class)) {    
    		LOGGER.info("processAnnotatedType: " + type.getAnnotatedType().getJavaClass().getName());
    	}
    } 
    
    void beforeBeanDiscovery(@Observes BeforeBeanDiscovery discovery) {
    	LOGGER.info("beginning the scanning process");
    }
    
    void afterBeanDiscovery(@Observes AfterBeanDiscovery discovery) {
    	LOGGER.info("finished the scanning process");
    }
    
	@SuppressWarnings("serial")
	void afterBeanDiscovery(@Observes AfterBeanDiscovery discovery, BeanManager manager) {
		
		if (bean == null) {
			
			LOGGER.info("Registering DocumentManagerFactory...");
			
			bean = new Bean<DocumentManagerFactory>() {
				
				DocumentManagerFactory instance = Datastore.createDocumentManagerFactory();

	            @Override
	            public Class<?> getBeanClass() {
	                return DocumentManagerFactory.class;
	            }

	            @Override
	            public Set<InjectionPoint> getInjectionPoints() {
	                return Collections.emptySet();
	            }

	            @Override
	            public String getName() {
	                return ConfigExtension.class.getSimpleName();
	            }

	            @Override
	            public Set<Annotation> getQualifiers() {
	                Set<Annotation> qualifiers = new HashSet<Annotation>();
	                qualifiers.add( new AnnotationLiteral<Any>() {} );
	                qualifiers.add( new AnnotationLiteral<Default>() {} );
	                return qualifiers;
	            }

	            @Override
	            public Class<? extends Annotation> getScope() {
	                return ApplicationScoped.class;
	            }

	            @Override
	            public Set<Class<? extends Annotation>> getStereotypes() {
	                return Collections.emptySet();
	            }

	            @Override
	            public Set<Type> getTypes() {
	                Set<Type> types = new HashSet<Type>();
	                types.add(DocumentManagerFactory.class);
	                types.add(Object.class);
	                return types;
	            }

	            @Override
	            public boolean isAlternative() {
	                return false;
	            }

	            @Override
	            public boolean isNullable() {
	                return false;
	            }

	            @Override
	            public DocumentManagerFactory create(CreationalContext<DocumentManagerFactory> context) {
	                return instance;
	            }

	            @Override
	            public void destroy(DocumentManagerFactory instance, CreationalContext<DocumentManagerFactory> context) {
	            	instance.close();
	            	context.release();
	            }
	        };
	        
	        discovery.addBean( bean );
		}
	}

}
