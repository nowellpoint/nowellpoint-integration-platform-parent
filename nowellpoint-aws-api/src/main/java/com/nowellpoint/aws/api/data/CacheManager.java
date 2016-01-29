package com.nowellpoint.aws.api.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

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
	
	/**
	 * 
	 * @return cache
	 */
	
	public Jedis getCache() {
		return jedis;
	}
	
	/**
	 * 
	 * @param key
	 * @param values
	 */
	
	public <T> void sadd(String key, Set<T> values) {
		Pipeline p = jedis.pipelined();		
		values.stream().forEach(m -> {
			try {
				p.sadd(key.getBytes(), serialize(m));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		p.sync();
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	
	public <T> Set<T> smembers(String key) {
		Set<T> results = new HashSet<T>();
		jedis.smembers(key.getBytes()).stream().forEach(m -> {
			try {
				results.add(deserialize(m));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		return results;
	}
	
	public void set(String key, Object value) throws IOException {
		jedis.set(key.getBytes(), serialize(value));
	}
	
	/**
	 * 
	 * @param key
	 * @param seconds
	 * @param value
	 * @throws IOException
	 */
	
	public void set(String key, int seconds, Object value) throws IOException {
		jedis.setex(key.getBytes(), seconds, serialize(value));
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	
	public <T> T get(String key) {
		byte[] bytes = jedis.get(key.getBytes());
		if (bytes != null) {
			try {
				return deserialize(bytes);
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		return null;
	}
	
	/**
	 * 
	 * @param key
	 */
	
	public void del(String key) {
		jedis.del(key.getBytes());
	}
	
	/**
	 * 
	 * @param key
	 * @param field
	 * @param values
	 */
	
	public <T> void hset(String key, String field, Set<T> values) {
		Pipeline p = jedis.pipelined();		
		values.stream().forEach(m -> {
			try {
				Method method = m.getClass().getMethod("get" + field.substring(0,1).toUpperCase() + field.substring(1));
				String id = (String) method.invoke(m, new Object[] {});
				p.hset(key.getBytes(), id.getBytes(), serialize(m));
				p.expire(key.getBytes(), 10);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		p.sync();
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	
	public <T> Set<T> hgetAll(String key) {
		Set<T> results = new HashSet<T>();
		jedis.hgetAll(key.getBytes()).values().stream().forEach(m -> {
			try {
				results.add(deserialize(m));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		return results;
	}
	
	/**
	 * 
	 * @param key
	 * @param field
	 */
	
	public void hdel(String key, String field) {
		jedis.hdel(key.getBytes(), field.getBytes());
	}
	
	/**
	 * 
	 * @param object
	 * @return
	 * @throws IOException
	 */
	
	private static byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(baos);
            os.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } finally {
        	try {
				baos.close();
			} catch (IOException ignore) {
				
			}
        }
    }
	
	/**
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	
	@SuppressWarnings("unchecked")
	private static <T> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object object = ois.readObject();
            return (T) object;
        } finally {
            try {
				bais.close();
			} catch (IOException ignore) {

			}
        }
	}
}