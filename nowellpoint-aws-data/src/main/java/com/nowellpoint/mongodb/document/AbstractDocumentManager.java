package com.nowellpoint.mongodb.document;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.mongodb.MongoException;
import com.mongodb.async.client.MongoCollection;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.annotation.Id;
import com.nowellpoint.mongodb.annotation.MappedSuperclass;

public abstract class AbstractDocumentManager {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractDocumentManager.class);
	
	private DocumentManagerFactory documentManagerFactory;
	
	public AbstractDocumentManager(DocumentManagerFactory documentManagerFactory) {
		this.documentManagerFactory = documentManagerFactory;
	}
	
	/**
	 *
	 * 
	 * @return
	 * 
	 * 
	 */
	
	protected DocumentManagerFactory getDocumentManagerFactory() {
		return documentManagerFactory;
	}
	
	/**
	 * 
	 * 
	 * @param documentClass
	 * @return
	 * 
	 * 
	 */
	
	protected <T> String resolveCollectionName(Class<T> documentClass) {
		return documentManagerFactory.resolveCollectionName(documentClass);
	}
	
	/**
	 * 
	 * 
	 * @param documentClass
	 * @return
	 * 
	 * 
	 */
	
	protected <T> MongoCollection<T> getCollection(Class<T> documentClass) {
		return documentManagerFactory.getCollection( documentClass );
	}
	
	/**
	 * 
	 * 
	 * @param bson
	 * @return
	 * 
	 * 
	 */
	
	protected String bsonToString(Bson bson) {
		return documentManagerFactory.bsonToString(bson);
	}
	
	/**
	 * 
	 * 
	 * @param object
	 * @return
	 * 
	 * 
	 */

	protected Object resolveId(Object object) {
		Object id = null;
		Set<Field> fields = getAllFields(object);
		for (Field field : fields) {
			if (field.isAnnotationPresent(Id.class)) {					
				id = getIdValue(object, field);
			}
		}
		return id;
	}

	/**
	 * 
	 * 
	 * @param object
	 * @return
	 * 
	 * 
	 */
	
	private Set<Field> getAllFields(Object object) {
		Set<Field> fields = new LinkedHashSet<Field>();
		if (object.getClass().getSuperclass().isAnnotationPresent(MappedSuperclass.class)) {
			fields.addAll(Arrays.asList(object.getClass().getSuperclass().getDeclaredFields()));
		}
		fields.addAll(Arrays.asList(object.getClass().getDeclaredFields()));
		return fields;
	}
	
	/**
	 * 
	 * 
	 * @param object
	 * @param field
	 * @return
	 * 
	 * 
	 */
	
	private Object getIdValue(Object object, Field field) {
		Object id = getFieldValue(object, field);
		if (field.getType().isAssignableFrom(ObjectId.class)) {
	    	id = new ObjectId(id.toString()); 	
	    } 
		return id;
	}
	
	/**
	 * 
	 * 
	 * @param object
	 * @param id
	 * 
	 * 
	 */
	
	protected void setIdValue(Object object, Object id) {
		Set<Field> fields = getAllFields(object);
		for (Field field : fields) {
			if (field.isAnnotationPresent(Id.class)) {					
				try {
					Method method = object.getClass().getMethod("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), new Class[] {field.getType()});
					method.invoke(object, new Object[] {id});
				} catch (NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}	
			}
		}
	}
	
	/**
	 * 
	 * 
	 * @param object
	 * @param field
	 * @return
	 * 
	 * 
	 */
	
	private Object getFieldValue(Object object, Field field) {
		try {
		    Method method = object.getClass().getMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), new Class[] {});			    
		    Object value = method.invoke(object, new Object[] {});
            if (field.getType().isAssignableFrom(Locale.class)) {            	
		    	value = String.valueOf(value);
		    }
            return value;
		} catch (NoSuchMethodException e) {
			LOGGER.info("Unable to find get method for mapped property field: " + field.getName());
			return null;
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * 
	 * @param exception
	 * 
	 * 
	 */
	
	private static void publish(MongoException exception) {
		AmazonSNS snsClient = new AmazonSNSClient();
		PublishRequest publishRequest = new PublishRequest("arn:aws:sns:us-east-1:600862814314:MONGODB_EXCEPTION", exception.getMessage());
		snsClient.publish(publishRequest);
	}
}
