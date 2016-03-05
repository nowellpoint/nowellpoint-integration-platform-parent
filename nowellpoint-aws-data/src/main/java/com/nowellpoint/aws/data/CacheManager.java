package com.nowellpoint.aws.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import com.nowellpoint.aws.model.admin.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Protocol;

@ApplicationScoped
public class CacheManager {
	
	private static final Logger LOGGER = Logger.getLogger(CacheManager.class.getName());
	private JedisPool jedisPool;
	
	@PostConstruct
	public void postConstruct() {
		String endpoint = System.getProperty(Properties.REDIS_HOST);
		Integer port = Integer.valueOf(System.getProperty(Properties.REDIS_PORT));
		
		JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(30);
		
		jedisPool = new JedisPool(poolConfig, endpoint, port, Protocol.DEFAULT_TIMEOUT, System.getProperty(Properties.REDIS_PASSWORD));
		
		LOGGER.info("connecting to cache...is connected: " + ! jedisPool.isClosed());
	}
	
	@PreDestroy
	public void preDestroy() {
		try {
			jedisPool.destroy();
        } catch (Exception e) {
        	LOGGER.warning(String.format("Cannot properly close Jedis pool %s", e.getMessage()));
        }
		jedisPool = null;
		
		LOGGER.info("disconnecting from cache...is connected: " + ! jedisPool.isClosed());
	}
	
	/**
	 * 
	 * @return cache
	 */
	
	public Jedis getCache() {
		return jedisPool.getResource();
	}
	
	/**
	 * 
	 * @param key
	 * @param values
	 */
	
	public <T> void sadd(String key, Set<T> values) {
		Jedis jedis = getCache();
		Pipeline p = jedis.pipelined();
		try {
			values.stream().forEach(m -> {
				try {
					p.sadd(key.getBytes(), serialize(m));
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
	 * @return
	 */
	
	public <T> Set<T> smembers(String key) {
		Jedis jedis = getCache();
		Set<T> results = new HashSet<T>();
		
		try {
			jedis.smembers(key.getBytes()).stream().forEach(m -> {
				results.add(deserialize(m));
			});
		} finally {
			jedis.close();
		}
		
		return results;
	}
	
	/**
	 * 
	 * @param key
	 * @param seconds
	 * @param value
	 * @throws IOException
	 */
	
	public <T> void setex(String key, int seconds, T value) {
		Jedis jedis = getCache();
		try {
			jedis.setex(key.getBytes(), seconds, serialize(value));
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	
	public <T> T get(String key) {
		Jedis jedis = getCache();
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
	 */
	
	public void del(String key) {
		Jedis jedis = getCache();
		try {
			jedis.del(key.getBytes());
		} finally {
			jedis.close();
		}
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	
	public <T> Set<T> hgetAll(String key) {
		Jedis jedis = getCache();
		Set<T> results = new HashSet<T>();
		try {
			jedis.hgetAll(key.getBytes()).values().stream().forEach(m -> {
				try {
					results.add(deserialize(m));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} finally {
			jedis.close();
		}
		
		return results;
	}
	
	/**
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	
	public <T> T hget(String key, String field) {
		Jedis jedis = getCache();
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