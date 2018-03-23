package com.nowellpoint.signup.provider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import com.nowellpoint.util.EnvironmentVariables;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

@ApplicationScoped
public class CacheProvider {
	
	//@Inject
	private Logger logger = Logger.getLogger(CacheProvider.class);
	
	private JedisPool jedisPool;
	
	public Jedis getCache() {
		return jedisPool.getResource();
	}
	
	public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
		System.out.println("init");
		JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(1000);
		
		jedisPool = new JedisPool(
				poolConfig, 
				System.getenv(EnvironmentVariables.REDIS_HOST), 
				Integer.valueOf(System.getenv(EnvironmentVariables.REDIS_PORT)), 
				Protocol.DEFAULT_TIMEOUT, 
				System.getenv(EnvironmentVariables.REDIS_PASSWORD));
		
		logger.info("connecting to cache...is connected: " + ! jedisPool.isClosed());
	}
	
	public void destroy(@Observes @Destroyed(ApplicationScoped.class) Object destroy) {
		try {
			jedisPool.destroy();
        } catch (Exception e) {
        	    logger.warn(String.format("Cannot properly close Jedis pool %s", e.getMessage()));
        }
		
		logger.info("disconnecting from cache...is connected: " + ! jedisPool.isClosed());
		
		jedisPool = null;
	}
}