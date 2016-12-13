package com.nowellpoint.data;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.nowellpoint.aws.model.admin.Properties;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class CacheTest {
	
	@BeforeClass
	public static void beforeClass() {
		Properties.setSystemProperties(System.getenv("NCS_PROPERTY_STORE"));
	}

	@Test
	@Ignore
	public void testClearCache() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(300);
		
        JedisPool jedisPool = new JedisPool(
				poolConfig, 
				System.getProperty(Properties.REDIS_HOST), 
				Integer.valueOf(System.getProperty(Properties.REDIS_PORT)), 
				Protocol.DEFAULT_TIMEOUT, 
				System.getProperty(Properties.REDIS_PASSWORD));
        
        System.out.println(jedisPool.getResource().info());
        
        jedisPool.getResource().flushAll();
        
        System.out.println(jedisPool.getResource().info());
        
        jedisPool.close();
	}
}