package com.nowellpoint.aws.api.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.nowellpoint.aws.model.admin.Properties;

import redis.clients.jedis.Jedis;

@WebListener
public class CacheManager implements ServletContextListener {
	
	private static final Logger log = Logger.getLogger(CacheManager.class.getName());
	private static Jedis jedis;
	
	static {
		jedis = new Jedis("pub-redis-10497.us-east-1-2.3.ec2.garantiadata.com", 10497);
	}
	
	@Override
	public void contextInitialized(ServletContextEvent event) {		
		jedis.auth(System.getProperty(Properties.REDIS_PASSWORD));
		log.info("connecting to cache...is connected: " + jedis.isConnected());
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		jedis.close();
		log.info("disconnecting from cache...is connected: " + jedis.isConnected());
	}
	
	public static Jedis getCache() {
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