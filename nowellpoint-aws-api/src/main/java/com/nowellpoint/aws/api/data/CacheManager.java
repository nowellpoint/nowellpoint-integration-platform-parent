package com.nowellpoint.aws.api.data;

import static redis.clients.jedis.ScanParams.SCAN_POINTER_START;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import com.nowellpoint.aws.model.admin.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

@ApplicationScoped
public class CacheManager {
	
	private static final Logger LOGGER = Logger.getLogger(CacheManager.class.getName());
	private Jedis jedis;
	
	@PostConstruct
	public void postConstruct() {
		String endpoint = System.getProperty(Properties.REDIS_ENDPOINT);
		Integer port = Integer.valueOf(System.getProperty(Properties.REDIS_PORT));
		
		jedis = new Jedis(endpoint, port);
		jedis.auth(System.getProperty(Properties.REDIS_PASSWORD));
		
		LOGGER.info("connecting to cache...is connected: " + jedis.isConnected());
	}
	
	@PreDestroy
	public void preDestroy() {
		jedis.close();
		LOGGER.info("disconnecting from cache...is connected: " + jedis.isConnected());
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
			results.add(deserialize(m));
		});
		
		return results;
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	
	public void set(String key, Object value) {
		jedis.set(key.getBytes(), serialize(value));
	}
	
	/**
	 * 
	 * @param key
	 * @param seconds
	 * @param value
	 * @throws IOException
	 */
	
	public <T> void setex(String key, int seconds, T value) {
		jedis.setex(key.getBytes(), seconds, serialize(value));
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	
	public <T> T get(String key) {
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
	 */
	
	public void del(String key) {
		jedis.del(key.getBytes());
	}
	
	/**
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @throws IOException
	 */
	
	public <T> void hset(String key, String field, T value) {
		jedis.hset(key.getBytes(), field.getBytes(), serialize(value));
	}
	
	/**
	 * 
	 * @param key
	 * @param type
	 * @return matching entries for Class<T>
	 */
	
	public <T> Set<T> hscanByClassType(String key, Class<T> type) {
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
	
	/**
	 * 
	 * @param key
	 * @param field
	 * @param values
	 */
	
	public <T> void hsetByClassType(String key, Set<T> values) {
		Pipeline p = jedis.pipelined();		
		values.stream().forEach(value -> {
			try {
				Method method = value.getClass().getMethod("getId");
				String id = (String) method.invoke(value, new Object[] {});
				p.hset(key.getBytes(), value.getClass().getName().concat(id).getBytes(), serialize(value));
			} catch (Exception e) {
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
	 * @param key
	 * @param field
	 * @return
	 */
	
	public <T> T hget(String key, String field) {
		byte[] bytes = jedis.hget(key.getBytes(), field.getBytes());
		T value = null;
		if (bytes != null) {
			value = deserialize(bytes);
		}
		return value;
	}
	
	/**
	 * 
	 * @param object
	 * @return
	 * @throws IOException
	 */
	
	public static byte[] serialize(Object object) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(baos);
            os.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (IOException e) {
        	LOGGER.severe("Cache serialize issue >>>");
			e.printStackTrace();
		} finally {
        	try {
				baos.close();
			} catch (IOException ignore) {
				
			}
        }
        
        return null;
    }
	
	/**
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object object = ois.readObject();
            return (T) object;
        } catch (IOException | ClassNotFoundException e) {
        	LOGGER.severe("Cache deserialize issue >>>");
			e.printStackTrace();
		} finally {
            try {
				bais.close();
			} catch (IOException ignore) {

			}
        }
        
        return null;
	}
}