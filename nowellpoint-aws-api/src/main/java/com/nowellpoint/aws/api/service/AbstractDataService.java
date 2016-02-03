package com.nowellpoint.aws.api.service;

import static com.nowellpoint.aws.api.data.CacheManager.deserialize;
import static com.nowellpoint.aws.api.data.CacheManager.serialize;
import static redis.clients.jedis.ScanParams.SCAN_POINTER_START;

import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import com.nowellpoint.aws.api.data.CacheManager;
import com.nowellpoint.aws.api.dto.AbstractDTO;

public abstract class AbstractDataService {
	
	@Inject
	private CacheManager cacheManager;
	
	protected ModelMapper modelMapper;
	
	public AbstractDataService() {
		
		modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		modelMapper.getConfiguration().setMethodAccessLevel(AccessLevel.PROTECTED); 
	}
	
	protected <T extends AbstractDTO> void hset(String key, String field, T value) {
		Jedis jedis = cacheManager.getCache();
		System.out.println(field.getBytes().length);
		Long result = jedis.hset(key.getBytes(), field.getBytes(), serialize(value));
		System.out.println("result: " + result);
	}
	
	protected void hdel(String key, String field) {
		Jedis jedis = cacheManager.getCache();
		jedis.hdel(key.getBytes(), field.getBytes());
	}
	
	public <T extends AbstractDTO> T hget(String key, String field) {
		Jedis jedis = cacheManager.getCache();
		jedis.hkeys(key).forEach(k -> System.out.println(k));
		byte[] bytes = jedis.hget(key.getBytes(), field.getBytes());
		T value = null;
		if (bytes != null) {
			value = deserialize(bytes);
		}
		return value;
	}
	
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
	
	protected <T  extends AbstractDTO> Set<T> hscan(String key, Class<T> type) {
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