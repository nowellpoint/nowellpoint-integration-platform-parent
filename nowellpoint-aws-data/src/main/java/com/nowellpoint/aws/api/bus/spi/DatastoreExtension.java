package com.nowellpoint.aws.api.bus.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessProducer;
import javax.enterprise.inject.spi.Producer;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

import com.nowellpoint.aws.data.annotation.Handler;

public class DatastoreExtension implements Extension {
	
	/**
     * 
     */
    
    private static final Logger LOG = Logger.getLogger(DatastoreExtension.class.getName());
    
    /**
     * 
     */
    
    private Bean<DocumentManagerFactory> dmfBean;
    
    /**
     * 
     */
    
    private Set<Object> collectionSet = new HashSet<Object>();
    
    /**
     * 
     * @param pp
     * @param bm
     */
    
    @SuppressWarnings("serial")
	void processProducer(@Observes ProcessProducer<?, DocumentManager> pp, final BeanManager bm) {
    	
    	if (pp.getAnnotatedMember().isAnnotationPresent(Handler.class)) {
    		
    		AnnotatedField<?> field = (AnnotatedField<?>) pp.getAnnotatedMember();
    		
    		final String datastoreName = field.getAnnotation(Handler.class).queueName()
    		final Class<? extends Field> module = field.getJavaMember().getClass();
    		final boolean alternative = field.isAnnotationPresent(Alternative.class);
    		final Set<Annotation> qualifiers = new HashSet<Annotation>();
    		for (Annotation annotation : field.getAnnotations()) {
    			Class<? extends Annotation> annotationType = annotation.annotationType();
    			if (annotationType.isAnnotationPresent(Qualifier.class)) {
    				qualifiers.add(annotation);
    			}
    		}
    		if (qualifiers.isEmpty()) {
    			qualifiers.add(new AnnotationLiteral<Default>() {});
    		}
    		qualifiers.add(new AnnotationLiteral<Any>() {});
    		final Set<Type> types = new HashSet<Type>()  {
				{
    				add(DocumentManagerFactory.class);
    				add(Object.class);
    			}
    		};
    		
    		if (dmfBean == null) {
    			
    			dmfBean = new Bean<DocumentManagerFactory>() {

					@Override
					public DocumentManagerFactory create(CreationalContext<DocumentManagerFactory> context) {
						return Datastore.createDocumentManagerFactory(datastoreName, collectionSet);
					}

					@Override
					public void destroy(DocumentManagerFactory dmf, CreationalContext<DocumentManagerFactory> context) {
						dmf.close();
						context.release();
					}

					@Override
					public Class<?> getBeanClass() {
						return module;
					}

					@Override
					public Set<InjectionPoint> getInjectionPoints() {
						return Collections.emptySet();
					}

					@Override
					public String getName() {
						return null;
					}

					@Override
					public Set<Annotation> getQualifiers() {
						return qualifiers;
					}

					@Override
					public Class<? extends Annotation> getScope() {
						return ApplicationScoped.class;
					}

					@Override
					public Set<Class<? extends Annotation>> getStereotypes() {
						return null;
					}

					@Override
					public Set<Type> getTypes() {
						return types;
					}

					@Override
					public boolean isAlternative() {
						return alternative;
					}

					@Override
					public boolean isNullable() {
						return false;
					}		
    			};
    		} else {
    			throw new RuntimeException("Only one DocumentManagerFactory per application is allowed");
    		}
    		Producer<DocumentManager> producer = new Producer<DocumentManager>() {

				@Override
				public void dispose(DocumentManager documentManager) {
					
				}

				@Override
				public Set<InjectionPoint> getInjectionPoints() {
					return Collections.emptySet();
				}
				
				private DocumentManagerFactory getDocumentManagerFactory(CreationalContext<DocumentManager> context) {
					return (DocumentManagerFactory) bm.getReference(dmfBean, DocumentManagerFactory.class, context);
				}

				@Override
				public DocumentManager produce(CreationalContext<DocumentManager> context) {
					return getDocumentManagerFactory(context).createDocumentManager();
				}
    			
    		};
    		pp.setProducer(producer);
    	}
    }
    
    void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd) {
    	LOG.info("beginning the scanning process");
    }
    
    void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
    	abd.addBean(dmfBean);
    	LOG.info("finished the scanning process");
    }
    
    <T> void processAnnotatedType(@Observes ProcessAnnotatedType<T> pat, BeanManager beanManager) {
    	if (pat.getAnnotatedType().getJavaClass().isAnnotationPresent(Document.class)) {    		
    		collectionSet.add(pat.getAnnotatedType().getJavaClass());
    	}
    } 
}