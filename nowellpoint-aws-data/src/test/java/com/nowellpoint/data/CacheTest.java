package com.nowellpoint.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.aws.model.admin.Properties;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class CacheTest {
	
	@BeforeClass
	public static void beforeClass() {
		Properties.setSystemProperties(System.getenv("NCS_PROPERTY_STORE"));
	}

	@Test
	@Ignore
	public void testClearCache() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(300);
		
        JedisPool jedisPool = new JedisPool(
				poolConfig, 
				System.getProperty(Properties.REDIS_HOST), 
				Integer.valueOf(System.getProperty(Properties.REDIS_PORT)), 
				Protocol.DEFAULT_TIMEOUT, 
				System.getProperty(Properties.REDIS_PASSWORD));
        
        System.out.println(jedisPool.getResource().info());
        
        jedisPool.getResource().flushAll();
        
        System.out.println(jedisPool.getResource().info());
        
        jedisPool.close();
	}
	
	@Test
	public void testMongoClusterConnect() {
		MongoClientOptions.Builder builder = MongoClientOptions.builder()
                .sslEnabled(true)
                .sslInvalidHostNameAllowed(true);
		
		MongoClientURI uri = new MongoClientURI(System.getenv("MONGO_CLIENT_URI"));
		List<ServerAddress> hosts = uri.getHosts().stream().map(p-> new ServerAddress(p)).collect(Collectors.toList());
		List<MongoCredential> credentials = Arrays.asList(uri.getCredentials());
		MongoClient mongoClient = new MongoClient(hosts, credentials, builder.build());
		MongoDatabase database = mongoClient.getDatabase(uri.getDatabase());
		System.out.println(database.getName());
		System.out.println(database.getCollection("account.profiles").count());
		mongoClient.close();
	}
}