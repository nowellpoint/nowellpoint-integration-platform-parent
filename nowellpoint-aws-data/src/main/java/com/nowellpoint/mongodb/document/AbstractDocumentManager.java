package com.nowellpoint.mongodb.document;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.nowellpoint.mongodb.DocumentManagerFactory;
import com.nowellpoint.mongodb.annotation.EmbedMany;
import com.nowellpoint.mongodb.annotation.EmbedOne;
import com.nowellpoint.mongodb.annotation.Id;
import com.nowellpoint.mongodb.annotation.MappedSuperclass;
import com.nowellpoint.mongodb.annotation.Reference;
import com.nowellpoint.mongodb.annotation.Transient;

/**
 * Abstract class for handling reflection to and from objects / bson 
 * and DocumentManagerFactory functions
 *
 * @author John Herson
 * @since 1.0.1
 */

public abstract class AbstractDocumentManager extends AbstractAsyncClient {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractDocumentManager.class);
	
	private final ConcurrentMap<String,Set<Field>> fieldCache = new ConcurrentHashMap<>();
	
	private final ConcurrentMap<Object,Object> referenceCache = new ConcurrentHashMap<>();
	
	protected static final String ID = "_id"; 			
	
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
	
	protected MongoCollection<Document> getCollection(Class<?> documentClass) {
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
				break;
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
	
	protected Document toBsonDocument(Object object) {
		Document document = new Document();
		Set<Field> fields = getAllFields(object);
		fields.stream().forEach(field -> {
			if (field.isAnnotationPresent(Id.class)) {
				document.put(ID, getIdValue(object, field));
			} else if (field.isAnnotationPresent(Reference.class)) {
				document.put(field.getName(), resolveId(getFieldValue( object, field )));
			} else if (field.isAnnotationPresent(EmbedOne.class)) {
				document.put(field.getName(), toBsonDocument(getFieldValue(object, field)));
			} else if (field.isAnnotationPresent(EmbedMany.class)) {
				document.put(field.getName(), toBsonArray(field, getFieldValue(object, field)));
			} else {	
				document.put(field.getName(), getFieldValue(object, field));
			}
		});
		
		return document;
	}
	
	protected List<Document> toBsonArray(Field field, Object object) {
		List<Document> documents = new ArrayList<>();
		Set<?> items = (Set<?>) object;
		items.stream().forEach(item -> {
			System.out.println(item.getClass().getName());
			documents.add(toBsonDocument(item));
		});
		
		return documents;
	}
	
	/**
	 * 
	 * 
	 * @param type
	 * @param bson
	 * @return
	 * 
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	protected <T> T toObject(Class<T> type, Document bson) {
		Object object = instantiate(type);
		toObject(object, bson);
		return (T) object;
	}
	
	/**
	 * 
	 * 
	 * @param object
	 * @param bson
	 * 
	 * 
	 */
	
	protected void toObject(Object object, Document bson) {
		Set<Field> fields = getAllFields(object);
		for (Field field : fields) {
			if (field.isAnnotationPresent(Id.class)) {
				setFieldValue(field.getDeclaringClass(), object, field, bson.get(ID));
			} else if (field.isAnnotationPresent(Reference.class)) {
		    	setFieldValue(field.getDeclaringClass(), object, field, parseReference(field, bson.get(field.getName())));
		    } else if (field.isAnnotationPresent(EmbedOne.class)) {
		    	setFieldValue(field.getDeclaringClass(), object, field, parseEmbedOne(field.getType(), bson.get(field.getName(), Document.class)));
		    } else if (field.isAnnotationPresent(EmbedMany.class)) {
		    	setFieldValue(field.getDeclaringClass(), object, field, parseEmbedMany(field, bson.get(field.getName(), ArrayList.class)));
			} else {
				setFieldValue(field.getDeclaringClass(), object, field, bson.get(field.getName()));
			}
		}
	}
	
	private <T> Set<T> parseEmbedMany(Field embeddedField, ArrayList<?> items) {
		if (items != null) {
			ParameterizedType type = (ParameterizedType) embeddedField.getGenericType();
			Class<?> embeddedClass = (Class<?>) type.getActualTypeArguments()[0];
			
			Set<T> list = new HashSet<>();
			items.stream().forEach(item -> {
				T object = parseEmbedOne(embeddedClass, (Document) item);
				list.add(object);
			});
			
			return list;
		}
		
		return null;
	}
	
	/**
	 * 
	 * 
	 * @param embeddedField
	 * @param bson
	 * @return
	 * 
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	private <T> T parseEmbedOne(Class<?> type, Document bson) {
		if (bson != null) {
			Object object = instantiate(type);
			Set<Field> fields = getAllFields(object);
			for (Field field : fields) {
				if (field.isAnnotationPresent(EmbedOne.class)) {
					setFieldValue(field.getDeclaringClass(), object, field, parseEmbedOne(field.getType(), bson.get(field.getName(), Document.class)));
				} else if (field.isAnnotationPresent(EmbedMany.class)) {
			    	setFieldValue(field.getDeclaringClass(), object, field, parseEmbedMany(field, bson.get(field.getName(), ArrayList.class)));	
				} else {
					setFieldValue(object.getClass(), object, field, bson.get(field.getName()));
				}
			};
			
			return (T) object;
		}
		
		return null;
	}
	
	/**
	 * 
	 * 
	 * @param referenceField
	 * @param value
	 * @return
	 * 
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	private <T> T parseReference(Field referenceField, Object value) {
		if (! referenceField.getType().isAnnotationPresent(com.nowellpoint.mongodb.annotation.Document.class)) {
    		throw new DocumentManagerException("Missing Document annotation from " + referenceField.getType().getClass().getName());
    	}
		
		if (value != null) {
			Object object = referenceCache.get(value);
			if (object == null) {
				object = instantiate(referenceField.getType());
				
				MongoCollection<Document> collection = documentManagerFactory.getCollection( documentManagerFactory.resolveCollectionName( referenceField.getType() ) );
				Document bson = findOne(collection, Filters.eq ( ID, value ));
				
				Set<Field> fields = getAllFields(object);
				for (Field field : fields) {
					if (! referenceField.getType().equals(field.getType())) {
						if (field.isAnnotationPresent(Id.class)) {
							setFieldValue(object.getClass(), object, field, bson.get(ID));
						} else if (field.isAnnotationPresent(EmbedOne.class)) {
							setFieldValue(field.getDeclaringClass(), object, field, parseEmbedOne(field.getType(), bson.get(field.getName(), Document.class)));
						} else if (field.isAnnotationPresent(EmbedMany.class)) {
					    	setFieldValue(field.getDeclaringClass(), object, field, parseEmbedMany(field, bson.get(field.getName(), ArrayList.class)));
						} else {
							setFieldValue(object.getClass(), object, field, bson.get(field.getName()));
						}
					}
				};
				
				referenceCache.put(value, object);
			}
			
			return (T) object;
		}
		
		return null;
		
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
		Set<Field> fields = null; //fieldCache.get(object.getClass().getName());
		if (fields == null) {
			fields = new LinkedHashSet<Field>();
			if (object.getClass().getSuperclass().isAnnotationPresent(MappedSuperclass.class)) {
				fields.addAll(Arrays.asList(object.getClass().getSuperclass().getDeclaredFields()));
			}
			fields.addAll(Arrays.asList(object.getClass().getDeclaredFields()));
			
			fields = fields.stream()
					.filter(field -> (! Modifier.isStatic(field.getModifiers()) && ! field.isAnnotationPresent(Transient.class)))
					.collect(Collectors.toSet());
			
			fieldCache.put(object.getClass().getName(), fields);
		} 
		
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
			if (id != null) {
				id = new ObjectId(id.toString()); 	
			} else {
				id = new ObjectId();
			}
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
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new DocumentManagerException(e);
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
		    System.out.println(method.getName());
		    Object value = method.invoke(object, new Object[] {});
            if (field.getType().isAssignableFrom(Locale.class)) {            	
		    	value = String.valueOf(value);
		    }
            return value;
		} catch (NoSuchMethodException e) {
			LOGGER.info("Unable to find get method for mapped property field: " + field.getName());
			return null;
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new DocumentManagerException(e);
		}
	}
	
	/**
	 * 
	 * 
	 * @param clazz
	 * @param object
	 * @param field
	 * @param value
	 * 
	 * 
	 */
	
	private void setFieldValue(Class<?> clazz, Object object, Field field, Object value) {
		if (value != null) {
			try {
			    Method method = clazz.getMethod("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), new Class[] {field.getType()});
			    if (field.getType().isAssignableFrom(Locale.class)) {
			    	value = Locale.forLanguageTag(value.toString());
			    }
			    method.invoke(object, new Object[] {value});
			} catch (NoSuchMethodException e) {
				LOGGER.info("Unable to find set method for mapped property field: " + field.getName());
				return;
			} catch (IllegalArgumentException e) {
				LOGGER.info("Illegal Argument for " + field.getName() + " invalid type " + value.getClass().getName());
				return;
			} catch (SecurityException | IllegalAccessException | InvocationTargetException e) {
				throw new DocumentManagerException(e);
			}
		}
	}
	
	/**
	 * 
	 * 
	 * @param type
	 * @return
	 * 
	 */
	
	protected <T> T instantiate(Class<T> type) {		
		try {
			return type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new DocumentManagerException(e);
		}
	}
}