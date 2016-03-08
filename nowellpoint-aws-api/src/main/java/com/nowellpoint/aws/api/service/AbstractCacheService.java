package com.nowellpoint.aws.api.service;

import static com.nowellpoint.aws.data.CacheManager.deserialize;
import static com.nowellpoint.aws.data.CacheManager.serialize;
import static redis.clients.jedis.ScanParams.SCAN_POINTER_START;

import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import javax.inject.Inject;

import com.nowellpoint.aws.api.dto.AbstractDTO;
import com.nowellpoint.aws.data.CacheManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public abstract class AbstractCacheService {
	
	@Inject
	private CacheManager cacheManager;
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	
	protected void set(String key, Object value) {
		Jedis jedis = cacheManager.getCache();
		try {
			jedis.set(key.getBytes(), serialize(value));
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
	
	protected <T extends AbstractDTO> T get(String key) {
		Jedis jedis = cacheManager.getCache();
		byte[] bytes = null;
		try {
			bytes = jedis.get(key.getBytes());
		} finally {
			jedis.close();
		}
		
		T value = null;
		if (bytes != null) {
			value = deserialize(bytes);
		}
		return value;
	}
	
	/**
	 * 
	 * @param key
	 * @param field
	 * @param value
	 */
	
	protected <T> void hset(String key, String field, T value) {
		Jedis jedis = cacheManager.getCache();
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
		Jedis jedis = cacheManager.getCache();
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
	
	public <T> T hget(String key, String field) {
		Jedis jedis = cacheManager.getCache();
		byte[] bytes = null;
		try {
			bytes = jedis.hget(key.getBytes(), field.getBytes());
		} finally {
			jedis.close();
		}
		
		T value = null;
		if (bytes != null) {
			value = deserialize(bytes);
		}
		
		return value;
	}
	
	/**
	 * 
	 * @param key
	 * @param values
	 */
	
	protected <T extends AbstractDTO> void hset(String key, Set<T> values) {
		Jedis jedis = cacheManager.getCache();
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
	 * @param type
	 * @return
	 */
	
	protected <T extends AbstractDTO> Set<T> hscan(String key, Class<T> type) {
		Jedis jedis = cacheManager.getCache();
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
			T t = deserialize(r.getValue());
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
		Jedis jedis = cacheManager.getCache();
		try {
			jedis.expire(key.getBytes(), seconds);
		} finally {
			jedis.close();
		}
	}
}