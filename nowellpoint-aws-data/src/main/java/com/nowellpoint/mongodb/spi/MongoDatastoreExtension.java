package com.nowellpoint.mongodb.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
import javax.enterprise.inject.spi.Producer;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.enterprise.util.AnnotationLiteral;

import org.bson.codecs.Codec;
import org.jboss.logging.Logger;

import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.document.DocumentManagerFactoryImpl;
import com.nowellpoint.util.Properties;

/**
 * <p>
 * The container fires an event of this type for each {@linkplain javax.enterprise.inject.Produces producer method or field} of
 * each enabled bean, including resources.
 * </p>
 * <p>
 * Any observer of this event is permitted to wrap and/or replace the {@code Producer}. The container must use the final value
 * of this property, after all observers have been called, whenever it calls the producer or disposer.
 * </p>
 * <p>
 * For example, this observer decorates the {@code Producer} for the all producer methods and field of type
 * {@code EntityManager}.
 * </p>
 * 
 * <pre>
 * void decorateEntityManager(@Observes ProcessProducer<?, EntityManager> pp) {
 *     pit.setProducer(decorate(pp.getProducer()));
 * }
 * </pre>
 * <p>
 * If any observer method of a {@code ProcessProducer} event throws an exception, the exception is treated as a definition error
 * by the container.
 * </p>
 * 
 * @see Producer
 * @author David Allen
 * @param <T> The bean class of the bean that declares the producer method or field
 * @param <X> The return type of the producer method or the type of the producer field
 */

public class MongoDatastoreExtension implements Extension {
	
	private static final Logger LOGGER = Logger.getLogger(MongoDatastoreExtension.class.getName());
	
	private List<Codec<?>> codecs = new ArrayList<Codec<?>>();
	
	private Bean<DocumentManagerFactory> bean;
    
    <T> void processAnnotatedType(@Observes @WithAnnotations({Document.class}) ProcessAnnotatedType<T> type, BeanManager beanManager) {
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
    
    void beforeBeanDiscovery(@Observes BeforeBeanDiscovery discovery) {
    	LOGGER.info("beginning the scanning process");
    }
    
    public void afterBeanDiscovery(@Observes AfterBeanDiscovery discovery) {
    	LOGGER.info("finished the scanning process");
    }
    
    /**
    * Returns the {@link javax.enterprise.inject.spi.AnnotatedField} representing the producer field or the
    * {@link javax.enterprise.inject.spi.AnnotatedMethod} representing the producer method.
    * 
    * @return the {@link javax.enterprise.inject.spi.AnnotatedMember} representing the producer
    */
    
	@SuppressWarnings("serial")
	void afterBeanDiscovery(@Observes AfterBeanDiscovery discovery, BeanManager manager) {
		
		LOGGER.info("Registering DocumentManagerFactory...");
		
		if (bean == null) {
			
			DocumentManagerFactory instance = new DocumentManagerFactoryImpl("mongodb://".concat(System.getProperty(Properties.MONGO_CLIENT_URI)), codecs);
			
			bean = new Bean<DocumentManagerFactory>() {

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
	                return MongoDatastoreExtension.class.getSimpleName();
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