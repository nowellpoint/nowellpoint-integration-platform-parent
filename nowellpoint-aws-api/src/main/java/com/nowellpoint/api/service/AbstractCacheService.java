package com.nowellpoint.api.service;

import static com.nowellpoint.aws.data.CacheManager.deserialize;
import static com.nowellpoint.aws.data.CacheManager.serialize;
import static redis.clients.jedis.ScanParams.SCAN_POINTER_START;

import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Optional;

import com.nowellpoint.api.model.dto.AbstractResource;
import com.nowellpoint.aws.data.CacheManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public abstract class AbstractCacheService {
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	
	protected void set(String key, Object value) {
		Jedis jedis = CacheManager.getCache();
		try {
			jedis.set(key.getBytes(), serialize(value));
		} finally {
			jedis.close();
		}
		
	}
	
	/**
	 * 
	 * @param key
	 * @param expire
	 * @param value
	 */
	
	protected void setex(String key, int expire, Object value) {
		Jedis jedis = CacheManager.getCache();
		try {
			jedis.setex(key.getBytes(), expire, serialize(value));
		} finally {
			jedis.close();
		}
		
	}
	
	/**
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 */
	
	protected <T> T get(Class<T> type, String key) {
		Jedis jedis = CacheManager.getCache();
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
	
	protected void del(String key) {
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
	 * @param field
	 * @param value
	 */
	
	protected <T> void hset(String key, String field, T value) {
		Jedis jedis = CacheManager.getCache();
		try {
			jedis.hset(key.getBytes(), field.getBytes(), serialize(value));
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param field
	 */
	
	protected void hdel(String key, String field) {
		Jedis jedis = CacheManager.getCache();
		try {
			jedis.hdel(key.getBytes(), field.getBytes());
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	
	protected <T> T hget(Class<T> type, String key, String field) {
		if (! Optional.ofNullable(type).isPresent()) {
			throw new IllegalArgumentException("missing type parameter");
		}
		
		if (! Optional.ofNullable(key).isPresent()) {
			throw new IllegalArgumentException("missing key parameter");
		}
		
		if (! Optional.ofNullable(field).isPresent()) {
			throw new IllegalArgumentException("missing field parameter");
		}
		
		Jedis jedis = CacheManager.getCache();
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
	 * @param key
	 * @param values
	 */
	
	protected <T extends AbstractResource> void hset(String key, Set<T> values) {
		Jedis jedis = CacheManager.getCache();
		Pipeline p = jedis.pipelined();	
		try {
			values.stream().forEach(value -> {
				try {
					p.hset(key.getBytes(), value.getClass().getName().concat(value.getId()).getBytes(), serialize(value));
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
	 * @param field
	 * @return
	 */
	
	protected Boolean hexists(String key, String field) {		
		if (! Optional.ofNullable(key).isPresent()) {
			throw new IllegalArgumentException("missing key parameter");
		}
		
		if (! Optional.ofNullable(field).isPresent()) {
			throw new IllegalArgumentException("missing field parameter");
		}
		
		Jedis jedis = CacheManager.getCache();
		
		try {
			return jedis.hexists(key.getBytes(), field.getBytes());
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
	
	protected <T> Set<T> hscan(String key, Class<T> type) {
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
			T t = deserialize(r.getValue(), type);
			results.add(t);
		});
		
		return results;
	}
	
	/**
	 * 
	 * @param key
	 * @param seconds
	 */
	
	protected void expire(String key, int seconds) {
		Jedis jedis = CacheManager.getCache();
		try {
			jedis.expire(key.getBytes(), seconds);
		} finally {
			jedis.close();
		}
	}
}