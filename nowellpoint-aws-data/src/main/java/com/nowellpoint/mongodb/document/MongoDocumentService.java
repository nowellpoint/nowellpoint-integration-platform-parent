package com.nowellpoint.mongodb.document;

import static com.mongodb.client.model.Filters.eq;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.nowellpoint.aws.data.AbstractCacheService;
import com.nowellpoint.mongodb.annotation.Id;
import com.nowellpoint.mongodb.annotation.MappedSuperclass;

public class MongoDocumentService extends AbstractCacheService {
	
	private static final Logger LOGGER = Logger.getLogger(MongoDocumentService.class);
	
	/**
	 * 
	 * 
	 * 
	 * 
	 */
	
	public MongoDocumentService() {		

	}
	
	/**
	 * 
	 * 
	 * @param type
	 * @return
	 * 
	 * 
	 */
	
	public <T> String resolveCollectionName(Class<T> type) {
		return MongoDatastore.resolveCollectionName(type);
	}
	
	/**
	 * 
	 * 
	 * @param <T>
	 * @param query
	 * @return
	 * 
	 * 
	 */
	
	public <T> FindIterable<T> find(Class<T> documentClass, Bson query) {
		
		FindIterable<T> search = null;
		
		try {
			
			search = MongoDatastore.getCollection( documentClass )
					.withDocumentClass( documentClass )
					.find( query );
			
		} catch (IllegalArgumentException e) {
			LOGGER.error( "query exception : ", e.getCause() );
		}
		
		return search;
	}
	
	/**
	 * 
	 * 
	 * @param query
	 * @return 
	 * @return
	 * 
	 * 
	 */
	
	public <T> T findOne(Class<T> documentClass, Bson query) {
		
		T document = null;
		
		try {
			
			document = MongoDatastore.getCollection( documentClass )
					.withDocumentClass( documentClass )
					.find( query )
					.first();
			
		} catch (IllegalArgumentException e) {
			LOGGER.error( "query exception : ", e.getCause());
		}
		
		if (document == null) {
			throw new DocumentNotFoundException(String.format( "Document of type: %s was not found: %s", documentClass.getSimpleName(), toString(query) ) );
		}
		
		return document;
	}
	
	/**
	 * 
	 * 
	 * @param id
	 * @return
	 * 
	 * 
	 */
	
	public <T> T find(Class<T> documentClass, Object id) {	
		
		T document = get(documentClass, id.toString());
		
		if (document == null) {
			try {
				
				document = MongoDatastore.getCollection( documentClass )
						.withDocumentClass( documentClass )
						.find( eq ( "_id", id ) )
						.first();
				
				if (document == null) {
					throw new DocumentNotFoundException(String.format( "Resource of type: %s for Id: %s was not found", documentClass.getSimpleName(), id.toString() ) );
				}
				
				set(id.toString(), document);
				
			} catch (IllegalArgumentException e) {
				LOGGER.error( "query exception : ", e.getCause());
			}
		} 

		return document;
	}
	
	/**
	 * 
	 * 
	 * @param document
	 * 
	 * 
	 */
	
	public <T> void create(T document) {	
		
		setIdValue(document, new ObjectId());
		
		Object id = resolveId(document);
		
		set(id.toString(), document);
		
		try {

			Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {					
					@SuppressWarnings("unchecked")
					MongoCollection<T> collection = (MongoCollection<T>) MongoDatastore.getCollection( document.getClass() );
			        collection.insertOne(document);
				}
			});
			
		} catch (MongoException e) {
			LOGGER.error( "insert one exception", e.getCause());
			publish(e);
			throw e;
		}	
	}
	
	/**
	 * 
	 * 
	 * @param document
	 * 
	 * 
	 */
	
	public <T> void replace(T document) {
		
		Object id = resolveId(document);
		
		set(id.toString(), document);
		
		try {
			
			Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {
					@SuppressWarnings("unchecked")
					MongoCollection<T> collection = (MongoCollection<T>) MongoDatastore.getCollection( document.getClass() );
					collection.replaceOne( Filters.eq ( "_id", id ), document );
				}
			});
			
		} catch (MongoException e) {
			LOGGER.error( "replace one exception", e.getCause());
			publish(e);
			throw e;
		}
	}
	
	/**
	 * 
	 * 
	 * @param document
	 * 
	 * 
	 */
	
	public <T> void delete(T document) {
		
		Object id = resolveId(document);
		
		del(id.toString());
		
		try {
			
			Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {
					@SuppressWarnings("unchecked")
					MongoCollection<T> collection = (MongoCollection<T>) MongoDatastore.getCollection( document.getClass() );
					collection.deleteOne(  Filters.eq ( "_id", id ) );
				}
			});
			
		} catch (MongoException e) {
			LOGGER.error( "delete one exception", e.getCause() );
			publish(e);
			throw e;
		}
	}
	
	/**
	 * 
	 *
	 * @param collectionName
	 * @param query
	 * 
	 * 
	 */
	
	public <T> void deleteMany(Class<T> documentClass, Bson query) {
		
		try {
			
			Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {
					MongoCollection<T> collection = (MongoCollection<T>) MongoDatastore.getCollection( documentClass );
					collection.deleteMany( query );
				}
			});
		
		} catch (MongoException e) {
			LOGGER.error( "delete many exception", e.getCause() );
			publish(e);
			throw e;
		}
		
	}
	
	/**
	 * 
	 * 
	 * @param src
	 * @return
	 * 
	 * 
	 */
	
	public static String encode(String src) {
		return Base64.getEncoder().encodeToString(src.getBytes());
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
	
	private void setIdValue(Object object, Object id) {
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
	 * @param bson
	 * @return
	 * 
	 * 
	 */
	
	private String toString(Bson bson) {
		return bson.toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry()).toString();
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