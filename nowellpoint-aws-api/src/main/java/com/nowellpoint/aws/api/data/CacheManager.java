package com.nowellpoint.aws.api.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.aws.model.admin.Properties;

@ApplicationScoped
public class CacheManager {
	
	private static final Logger log = Logger.getLogger(CacheManager.class.getName());
	private static final ObjectMapper mapper = new ObjectMapper();
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
	
	public <T> Set<T> smembers(String key) {
		Set<T> members = new HashSet<T>();
		jedis.smembers(key.getBytes()).stream().forEach(m -> {
			System.out.println(m.length);
			members.add(deserialize(m));
		});
		return members;
	}
	
	public void sadd(String key, Object... values) {
		Pipeline p = jedis.pipelined();		
		Arrays.asList(values).stream().forEach(m -> {
			try {
				p.sadd(key, mapper.writeValueAsString(m));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		p.sync();
	}
	
	public <T> Set<T> smembers(String key, Class<T> type) {
		Set<T> results = new HashSet<T>();
		jedis.smembers(key).stream().forEach(m -> {
			try {
				results.add(mapper.readValue(m, type));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		return results;
	}
	
	public void set(String key, Object value) {
		jedis.set(key.getBytes(), serialize(value));
	}
	
	public void set(String key, int seconds, Object value) {
		jedis.setex(key.getBytes(), seconds, serialize(value));
	}
	
	public <T> T get(String key) {
		byte[] bytes = jedis.get(key.getBytes());
		if (bytes != null) {
			return deserialize(bytes);
		} 
		return null;
	}
	
	public void del(String key) {
		jedis.del(key.getBytes());
	}
	
	private static byte[] serialize(Object object) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(baos);
            os.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        return null;
    }
	
	@SuppressWarnings("unchecked")
	private static <T> T deserialize(byte[] bytes) {
		ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object object = ois.readObject();
            return (T) object;
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            try {
				bais.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        return null;
	}
}