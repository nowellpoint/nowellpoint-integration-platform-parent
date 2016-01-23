package com.nowellpoint.aws.api.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import redis.clients.jedis.Jedis;

import com.nowellpoint.aws.model.admin.Properties;

@ApplicationScoped
public class CacheManager {
	
	private static final Logger log = Logger.getLogger(CacheManager.class.getName());
	private Jedis jedis;
	
	@PostConstruct
	public void postConstruct() {
		String endpoint = System.getProperty(Properties.REDIS_ENDPOINT);
		Integer port = Integer.valueOf(System.getProperty(Properties.REDIS_PORT));
		
		jedis = new Jedis(endpoint, port);
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
	
	public void set(String key, Object value) {
		jedis.set(key.getBytes(), serialize(value));
	}
	
	public void set(String key, int seconds, Object value) {
		jedis.setex(key.getBytes(), seconds, serialize(value));
	}
	
	public <T> T get(Class<T> type, String key) {
		byte[] bytes = jedis.get(key.getBytes());
		if (bytes != null) {
			return deserialize(type, bytes);
		} 
		return null;
	}
	
	public <T> List<T> getList(Class<T> type, String key) {
		byte[] bytes = jedis.get(key.getBytes());
		if (bytes != null) {
			return deserializeList(type, bytes);
		} 
		return null;
	}
	
	private static byte[] serialize(Object object) {
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
	
	@SuppressWarnings("unchecked")
	private static <T> List<T> deserializeList(Class<T> type, byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (List<T>) ois.readObject();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T deserialize(Class<T> type, byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
	}
}