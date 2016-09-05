package com.nowellpoint.aws.data.mongodb;

import static com.mongodb.client.model.Filters.eq;
import static redis.clients.jedis.ScanParams.SCAN_POINTER_START;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.nowellpoint.aws.data.CacheManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public abstract class MongoDocumentService<T extends MongoDocument> {
	
	protected static final ObjectMapper objectMapper = new ObjectMapper();
	
	private static final Logger LOGGER = Logger.getLogger(MongoDocumentService.class);
	
	private final Class<T> documentClass;
	
	/**
	 * 
	 * @param resourceType
	 * @param documentType
	 */
	
	public MongoDocumentService(Class<T> documentClass) {		
		this.documentClass = documentClass;
	}
	
	protected Set<T> find(Bson query) {
		Set<T> documents = null; //hscan(documentClass, toString(query));
		
		if (documents == null) {
			try {
				documents = MongoDatastore.find(documentClass, query);
				//hset(toString(query), documents);
			} catch (IllegalArgumentException e) {
				
			}
		}
		
		return documents;
	}
	
	protected T findOne(Bson query) {
		T document = null; //get(documentClass, toString(query));
		
		if (document == null) {
			try {
				document = MongoDatastore.findOne(documentClass, query);
				//set(toString(query), document);
			} catch (IllegalArgumentException e) {
				
			}
		}

		return document;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	protected T findById(String id) {	
		T document = get(documentClass, id);
		
		if (document == null) {
			try {
				document = MongoDatastore.findById(documentClass, new ObjectId(id));
				set(id, document);
			} catch (IllegalArgumentException e) {
				
			}
		}

		return document;
	}
	
	/**
	 * 
	 * @param owner
	 * @return Collection of documents for owner
	 */
	
	protected Set<T> findAllByOwner(String owner) {		
		Set<T> documents = hscan(documentClass, owner);
		
		if (documents == null || documents.isEmpty()) {
			documents = MongoDatastore.find(documentClass, eq ( "owner.href", owner ));
			hset( owner, documents );
		}
		
		return documents; 
	}
	
	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param eventSource
	 * @return the created Document
	 * @throws MongoException
	 */
	
	protected T create(String subject, T document) {	
		document.setId(new ObjectId());
		document.setCreatedById(subject);
		document.setLastModifiedById(subject);
		document.setSystemCreationDate(Date.from(Instant.now()));
		document.setSystemModifiedDate(Date.from(Instant.now()));
		
		if (document.getCreatedDate() == null) {
			document.setCreatedDate(Date.from(Instant.now()));
			document.setLastModifiedDate(Date.from(Instant.now()));
		}
		
		set(document.getId().toString(), document);
		hset(subject, document);
		
		try {
			MongoDatastore.insertOne( document );
		} catch (MongoException e) {
			LOGGER.error( "Create Document exception", e.getCause());
			throw e;
		}
		
		return document;		
	}
	
	/**
	 * 
	 * @param document
	 * @return the document that has been replaced
	 * @throws MongoException
	 */
	
	protected T replace(String subject, T document) {
		document.setLastModifiedById(subject);
		document.setLastModifiedDate(Date.from(Instant.now()));
		document.setSystemModifiedDate(Date.from(Instant.now()));
		
		set(document.getId().toString(), document);
		hset(subject, document);
		
		try {
			MongoDatastore.replaceOne( document );
		} catch (MongoException e) {
			LOGGER.error( "Update Document exception", e.getCause());
			throw e;
		}
		
		return document;
	}
	
	/**
	 * 
	 * @param document
	 * @throws MongoException
	 */
	
	protected void delete(String subject, T document) {
		del(document.getId().toString());
		hdel(subject, document);
		
		try {
			MongoDatastore.deleteOne( document );
		} catch (MongoException e) {
			LOGGER.error( "Delete Document exception", e.getCause());
			throw e;
		}
	}
	
	/**
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 */
	
	private static <T> T get(Class<T> type, String key) {
		Jedis jedis = CacheManager.getCache();
		byte[] bytes = null;
		try {
			bytes = jedis.get(key.getBytes());
		} finally {
			jedis.close();
		}
		
		T value = null;
		if (bytes != null) {
			value = CacheManager.deserialize(bytes, type);
		} else {
			LOGGER.warn("Cache miss: " + type + " " + key);
		}
		return value;
	}
	
	/**
	 * 
	 * @param key
	 */
	
	private static void del(String key) {
		Jedis jedis = CacheManager.getCache();
		try {
			jedis.del(key.getBytes());
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	
	private static void set(String key, Object value) {
		Jedis jedis = CacheManager.getCache(); //jedisPool.getResource();
		try {
			jedis.set(key.getBytes(), CacheManager.serialize(value));
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param type
	 * @return
	 */
	
	private static <T> Set<T> hscan(Class<T> type, String key) {
		Jedis jedis = CacheManager.getCache();
		ScanParams params = new ScanParams();
	    params.match(type.getName().concat("*"));
		
		ScanResult<Entry<byte[], byte[]>> scanResult = null;
		
		try {
			scanResult = jedis.hscan(key.getBytes(), SCAN_POINTER_START.getBytes(), params);
		} finally {
			jedis.close();
		}
		
		Set<T> results = new HashSet<T>();
		
		scanResult.getResult().forEach(r -> {
			T t = CacheManager.deserialize(r.getValue(), type);
			results.add(t);
		});
		
		return results;
	}
	
	private static <T> void clearCache(Class<T> type) {
		
	}
	
	/**
	 * 
	 * @param key
	 * @param values
	 */
	
	private static <T extends MongoDocument> void hset(String key, Set<T> values) {
		Jedis jedis = CacheManager.getCache();
		Pipeline p = jedis.pipelined();	
		try {
			values.stream().forEach(value -> {
				try {
					p.hset(key.getBytes(), value.getClass().getName().concat(":").concat(value.getId().toString()).getBytes(), CacheManager.serialize(value));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}); 
		} finally {
			jedis.close();
		}
		
		p.sync();
	}
	
	private static <T extends MongoDocument> void hset(String key, T value) {
		Jedis jedis = CacheManager.getCache(); //jedisPool.getResource();
		try {
			jedis.hset(key.getBytes(), value.getClass().getName().concat(":").concat(value.getId().toString()).getBytes(), CacheManager.serialize(value));
		} finally {
			jedis.close();
		}
	}
	
	private static <T extends MongoDocument> void hdel(String key, T value) {
		Jedis jedis = CacheManager.getCache();
		try {
			jedis.hdel(key.getBytes(), value.getClass().getName().concat(":").concat(value.getId().toString()).getBytes());
		} finally {
			jedis.close();
		}
	}
	
	private String toString(Bson bson) {
		String key = bson.toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry()).entrySet().stream().map(k -> k.toString()).collect(Collectors.joining(":"));
		System.out.println(key);
		return bson.toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry()).toString();
	}
}