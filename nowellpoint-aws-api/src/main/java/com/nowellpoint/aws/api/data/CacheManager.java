package com.nowellpoint.aws.api.data;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import redis.clients.jedis.Jedis;

@WebListener
public class CacheManager implements ServletContextListener {
	
	private static final Logger log = Logger.getLogger(CacheManager.class.getName());
	private static Jedis jedis;
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		jedis.close();
		log.info("disconnecting from cache...is connected: " + jedis.isConnected());
	}
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		jedis = new Jedis("pub-redis-10497.us-east-1-2.3.ec2.garantiadata.com", 10497);
		jedis.auth(System.getenv("REDIS_PASSWORD"));
		log.info("connecting to cache...is connected: " + jedis.isConnected());
	}
	
	public static Jedis getCache() {
		return jedis;
	}
}