package com.nowellpoint.aws;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import redis.clients.jedis.Jedis;

public class CacheManager {

	private Jedis jedis;
	
	public CacheManager(String endpoint, Integer port) {
		jedis = new Jedis(endpoint, port);
	}
	
	public void auth(String password) {		
		jedis.auth(password);
	}
	
	public void close() {
		jedis.close();
	}
	
	public Jedis getCache() {
		return jedis;
	}
	
	public void set(String key, Object value) {
		jedis.set(key.getBytes(), serialize(value));
	}
	
	public void setex(String key, int seconds, Object value) {
		jedis.setex(key.getBytes(), seconds, serialize(value));
	}
	
	public byte[] serialize(Object object) {
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
	
	public Object deserialize(byte[] bytes) {
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