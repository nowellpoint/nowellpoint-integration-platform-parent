package com.nowellpoint.aws.api.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

import redis.clients.jedis.Jedis;

import com.nowellpoint.aws.model.admin.Properties;

@Startup
@ApplicationScoped
public class CacheManager {
	
	private static final Logger log = Logger.getLogger(CacheManager.class.getName());
	private Jedis jedis;
	
	@PostConstruct
	public void postConstruct() {
		log.info("post construct");
		//jedis = new Jedis(
		//		System.getProperty(Properties.REDIS_ENDPOINT), 
		//		Integer.valueOf(System.getProperty(Properties.REDIS_PORT)));
		
		jedis = new Jedis(
				"pub-redis-10497.us-east-1-2.3.ec2.garantiadata.com", 
				10497);
		
		jedis.auth(System.getProperty(Properties.REDIS_PASSWORD));
		log.info("connecting to cache...is connected: " + jedis.isConnected());
	}
	
	@PreDestroy
	public void preDestroy() {
		jedis.close();
		log.info("disconnecting from cache...is connected: " + jedis.isConnected());
	}
	
	public Jedis getCache() {
		return jedis;
	}
	
	public static byte[] serialize(Object object) {
		ObjectOutputStream os = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(baos);
            os.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
	
	public static Object deserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
	}
}