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
		jedis.set(key.getBytes(), serialize(value));
	}
	
	/**
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 */
	
	protected <T extends AbstractDTO> T get(String key) {
		Jedis jedis = cacheManager.getCache();
		byte[] bytes = jedis.get(key.getBytes());
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
	
	protected <T extends AbstractDTO> void hset(String key, String field, T value) {
		Jedis jedis = cacheManager.getCache();
		jedis.hset(key.getBytes(), field.getBytes(), serialize(value));
	}
	
	/**
	 * 
	 * @param key
	 * @param field
	 */
	
	protected void hdel(String key, String field) {
		Jedis jedis = cacheManager.getCache();
		jedis.hdel(key.getBytes(), field.getBytes());
	}
	
	/**
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	
	public <T extends AbstractDTO> T hget(String key, String field) {
		Jedis jedis = cacheManager.getCache();
		byte[] bytes = jedis.hget(key.getBytes(), field.getBytes());
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
		values.stream().forEach(value -> {
			try {
				p.hset(key.getBytes(), value.getClass().getName().concat(value.getId()).getBytes(), serialize(value));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
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
		
		ScanResult<Entry<byte[], byte[]>> scanResult = jedis.hscan(key.getBytes(), SCAN_POINTER_START.getBytes(), params);
		
		Set<T> results = new HashSet<T>();
		
		scanResult.getResult().forEach(r -> {
			T t = deserialize(r.getValue());
			results.add(t);
		});
		
		return results;
	}

}
