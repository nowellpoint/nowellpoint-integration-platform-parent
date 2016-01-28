package com.nowellpoint.aws.test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.nowellpoint.aws.model.admin.Properties;
import com.nowellpoint.aws.model.admin.PropertyStore;
import com.nowellpoint.aws.model.data.Project;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class CacheTest {
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	@BeforeClass
	public static void before() {
		
		Properties.setSystemProperties(PropertyStore.SANDBOX);
	}

	
	@Test
	public void testAddListToCache() {
		
		MongoClientURI mongoClientURI = new MongoClientURI("mongodb://".concat(System.getProperty(Properties.MONGO_CLIENT_URI)));
		MongoClient mongoClient = new MongoClient(mongoClientURI);
		MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoClientURI.getDatabase());
		
		FindIterable<Document> iterable = mongoDatabase.getCollection( "projects" ).find();
		Set<Project> results = new HashSet<Project>();
		
		iterable.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		        try {
					results.add(mapper.readValue(document.toJson(), Project.class));
				} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		});
		
		Jedis jedis = new Jedis(System.getProperty(Properties.REDIS_ENDPOINT), Integer.valueOf(System.getProperty(Properties.REDIS_PORT)));
		jedis.auth(System.getProperty(Properties.REDIS_PASSWORD));
		
		jedis.del("test");
		
		StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toSet());
		
		long start = System.currentTimeMillis();
		
		Pipeline p = jedis.pipelined();
		
		results.stream().forEach(m -> {
			try {
				p.sadd("test", mapper.writeValueAsString(m));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		p.sync();
		
		System.out.println("sadd: " + (System.currentTimeMillis() - start));
		
		start = System.currentTimeMillis();
		
		results.clear();
		
		jedis.smembers("test").stream().forEach(m -> {
			try {
				results.add(mapper.readValue(m, Project.class));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		System.out.println("smembers: " + (System.currentTimeMillis() - start));
		
		System.out.println(results.size());
		
		jedis.close();
		mongoClient.close();
		
	}
}