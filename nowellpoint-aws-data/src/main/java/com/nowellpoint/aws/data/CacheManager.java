package com.nowellpoint.aws.data;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.model.admin.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Protocol;

@ApplicationScoped
public class CacheManager {
	
	private static final Logger LOGGER = Logger.getLogger(CacheManager.class.getName());
	private static final ObjectMapper mapper = new ObjectMapper();
	
	private static JedisPool jedisPool;
	private static Cipher cipher;
	private static SecretKey secretKey;
	private static IvParameterSpec iv;
	
	@PostConstruct
	public void postConstruct() {
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
	
	@PreDestroy
	public void preDestroy() {
		try {
			jedisPool.destroy();
        } catch (Exception e) {
        	LOGGER.warning(String.format("Cannot properly close Jedis pool %s", e.getMessage()));
        }
		jedisPool = null;
		
		LOGGER.info("disconnecting from cache...is connected: " + ! jedisPool.isClosed());
	}
	
	/**
	 * 
	 * @return cache
	 */
	
	public Jedis getCache() {
		return jedisPool.getResource();
	}
	
	/**
	 * 
	 * @param key
	 * @param values
	 */
	
	public <T> void sadd(String key, Set<T> values) {
		Jedis jedis = getCache();
		Pipeline p = jedis.pipelined();
		try {
			values.stream().forEach(m -> {
				try {
					p.sadd(key.getBytes(), serialize(m));
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
	 * @param key
	 * @return
	 */
	
	public <T> Set<T> smembers(Class<T> type, String key) {
		Jedis jedis = getCache();
		Set<T> results = new HashSet<T>();
		
		try {
			jedis.smembers(key.getBytes()).stream().forEach(m -> {
				results.add(deserialize(m,type));
			});
		} finally {
			jedis.close();
		}
		
		return results;
	}
	
	/**
	 * 
	 * @param type
	 * @param key
	 * @return
	 */
	
	public <T> T get(Class<T> type, String key) {
		Jedis jedis = getCache();
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
	 * @param seconds
	 * @param value
	 */
	
	public <T> void setex(String key, int seconds, T value) {
		Jedis jedis = getCache();
		try {
			jedis.setex(key.getBytes(), seconds, serialize(value));
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 
	 * @param key
	 */
	
	public void del(String key) {
		Jedis jedis = getCache();
		try {
			jedis.del(key.getBytes());
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	
	public <T> Set<T> hgetAll(Class<T> type, String key) {
		Jedis jedis = getCache();
		Set<T> results = new HashSet<T>();
		try {
			jedis.hgetAll(key.getBytes()).values().stream().forEach(m -> {
				try {
					results.add(deserialize(m, type));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} finally {
			jedis.close();
		}
		
		return results;
	}
	
	/**
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	
	public <T> T hget(Class<T> type, String key, String field) {
		Jedis jedis = getCache();
		byte[] bytes = null;
		
		try {
			bytes = jedis.hget(key.getBytes(), field.getBytes());
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
	 * @param object
	 * @return
	 */
	
	public static byte[] serialize(Object object) {
		byte[] bytes = null;
		try {
			String json = mapper.writeValueAsString(object);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			bytes = cipher.doFinal(json.getBytes("UTF8"));
		} catch (JsonProcessingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			LOGGER.severe("Cache serialize issue >>>");
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
	
	public static <T> T deserialize(byte[] bytes, Class<T> type) {
		T object = null;
		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
			bytes = cipher.doFinal(bytes);
			object = mapper.readValue(bytes, type);
		} catch (IOException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			LOGGER.severe("Cache deserialize issue >>>");
			e.printStackTrace();
		}
		
		return object;
	}
}