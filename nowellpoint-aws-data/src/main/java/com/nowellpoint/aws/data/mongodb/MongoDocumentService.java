package com.nowellpoint.aws.data.mongodb;

import static com.mongodb.client.model.Filters.eq;
import static redis.clients.jedis.ScanParams.SCAN_POINTER_START;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import com.nowellpoint.aws.model.admin.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public abstract class MongoDocumentService<T extends MongoDocument> {
	
	protected static final ObjectMapper objectMapper = new ObjectMapper();
	
	private static final Logger LOGGER = Logger.getLogger(MongoDocumentService.class);
	
	private final Class<T> documentType;
	
	private static JedisPool jedisPool;
	private static Cipher cipher;
	private static SecretKey secretKey;
	private static IvParameterSpec iv;
	
	/**
	 * 
	 * @param resourceType
	 * @param documentType
	 */
	
	public MongoDocumentService(Class<T> documentType) {		
		this.documentType = documentType;
		
		String endpoint = System.getProperty(Properties.REDIS_HOST);
		Integer port = Integer.valueOf(System.getProperty(Properties.REDIS_PORT));
		
		JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(30);
		
		jedisPool = new JedisPool(poolConfig, endpoint, port, Protocol.DEFAULT_TIMEOUT, System.getProperty(Properties.REDIS_PASSWORD));
		
		String keyString = System.getProperty(Properties.CACHE_DATA_ENCRYPTION_KEY);
		
		try {
			byte[] key = keyString.getBytes("UTF-8");
			
			MessageDigest sha = MessageDigest.getInstance("SHA-512");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 32);
		    
		    secretKey = new SecretKeySpec(key, "AES");
		    
		    cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		    
		    byte[] ivBytes = new byte[cipher.getBlockSize()];
		    iv = new IvParameterSpec(ivBytes);
		    
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
		
		LOGGER.info("connecting to cache...is connected: " + ! jedisPool.isClosed());
	}
	
	public void destroy() {
		try {
			jedisPool.destroy();
        } catch (Exception e) {
        	LOGGER.warn(String.format("Cannot properly close Jedis pool %s", e.getMessage()));
        }
		
		LOGGER.info("disconnecting from cache...is connected: " + ! jedisPool.isClosed());
		
		jedisPool = null;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	
	protected T findById(String id) {	
		T document = get(documentType, id);
		
		if (document == null) {
			try {
				document = MongoDatastore.findById(documentType, new ObjectId(id));
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
		Set<T> documents = hscan(documentType, owner);
		
		if (documents == null || documents.isEmpty()) {
			documents = MongoDatastore.find(documentType, eq ( "owner.href", owner ));
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
		document.setSystemModifiedDate(Date.from(Instant.now()));
		
		set(document.getId().toString(), document);
		
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
	
	protected void delete(T document) {
		del(document.getId().toString());
		
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
		Jedis jedis = jedisPool.getResource();
		byte[] bytes = null;
		try {
			bytes = jedis.get(key.getBytes());
		} finally {
			jedis.close();
		}
		
		T value = null;
		if (bytes != null) {
			value = deserialize(bytes, type);
		}
		return value;
	}
	
	/**
	 * 
	 * @param key
	 */
	
	private static void del(String key) {
		Jedis jedis = jedisPool.getResource();
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
		Jedis jedis = jedisPool.getResource();
		try {
			jedis.set(key.getBytes(), serialize(value));
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
		Jedis jedis = jedisPool.getResource();
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
			T t = deserialize(r.getValue(), type);
			results.add(t);
		});
		
		return results;
	}
	
	/**
	 * 
	 * @param key
	 * @param values
	 */
	
	private static <T extends MongoDocument> void hset(String key, Set<T> values) {
		Jedis jedis = jedisPool.getResource();
		Pipeline p = jedis.pipelined();	
		try {
			values.stream().forEach(value -> {
				try {
					p.hset(key.getBytes(), value.getClass().getName().concat(":").concat(value.getId().toString()).getBytes(), serialize(value));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}); 
		} finally {
			jedis.close();
		}
		
		p.sync();
	}
	
	/**
	 * 
	 * @param object
	 * @return
	 */
	
	private static byte[] serialize(Object object) {
		byte[] bytes = null;
		try {
			String json = Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(object).getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			bytes = cipher.doFinal(json.getBytes("UTF8"));
		} catch (JsonProcessingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			LOGGER.error("Cache serialize issue >>>");
			e.printStackTrace();
		}
        
		return bytes;
    }
	
	/**
	 * 
	 * @param bytes
	 * @param type
	 * @return
	 */
	
	private static <T> T deserialize(byte[] bytes, Class<T> type) {
		T object = null;
		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
			bytes = cipher.doFinal(bytes);
			object = objectMapper.readValue(Base64.getDecoder().decode(bytes), type);
		} catch (IOException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			LOGGER.error("Cache deserialize issue >>>");
			e.printStackTrace();
		}
		
		return object;
	}
}