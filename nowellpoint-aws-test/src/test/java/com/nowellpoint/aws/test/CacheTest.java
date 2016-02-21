package com.nowellpoint.aws.test;

import static redis.clients.jedis.ScanParams.SCAN_POINTER_START;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

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
//		
//		StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toSet());
		
		long start = System.currentTimeMillis();
		
		Pipeline p = jedis.pipelined();
//		
//		results.stream().forEach(m -> {
//			try {
//				p.sadd("test".getBytes(), serialize(m));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
//		
//		p.sync();
//		
//		System.out.println("sadd: " + (System.currentTimeMillis() - start));
//		
//		start = System.currentTimeMillis();
//		
//		results.clear();
//		
//		jedis.smembers("test".getBytes()).stream().forEach(m -> {
//			try {
//				results.add(deserialize(m));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
//		
//		System.out.println("smembers: " + (System.currentTimeMillis() - start));
//		
//		System.out.println(results.size());
//		
//		try {
//			System.out.println("writing results");
//			System.out.println(mapper.writeValueAsString(results));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
		
		jedis.del("htest".getBytes());
		
		System.out.println(jedis.hlen("htest".getBytes()));
		
		start = System.currentTimeMillis();
		
		results.stream().forEach(m -> {
			try {
				p.hset("htest".getBytes(), m.getId().toString().getBytes(), serialize(m));
				Method method = m.getClass().getMethod("get" + "id".substring(0,1).toUpperCase() + "id".substring(1));
				String id = (String) method.invoke(m, new Object[] {});
				System.out.println(id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		p.sync();
		
		System.out.println("htest: " + (System.currentTimeMillis() - start));
		
		results.clear();
		
		jedis.hgetAll("htest".getBytes()).values().stream().forEach(m -> {
			try {
				results.add(deserialize(m));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		System.out.println(results.size());
		
		try {
			System.out.println("writing results");
			System.out.println(mapper.writeValueAsString(results));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		jedis.hdel("htest".getBytes(), results.stream().findFirst().get().getId().toString().getBytes());
		
		results.clear();
		
		jedis.hgetAll("htest".getBytes()).values().stream().forEach(m -> {
			try {
				results.add(deserialize(m));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		System.out.println(results.size());
		
		System.out.println("this is what i care about");
		
		jedis.del(System.getProperty(Properties.DEFAULT_SUBJECT).getBytes());
		
		results.stream().forEach(r -> {
			System.out.println(r.getId());
			jedis.hset(System.getProperty(Properties.DEFAULT_SUBJECT).getBytes(), Project.class.getName().concat(r.getId().toString()).getBytes(), serialize(r));
		});
		
		ScanParams params = new ScanParams();
	    params.match(Project.class.getName().concat("*"));
		
		ScanResult<Entry<byte[], byte[]>> scanResult = jedis.hscan(System.getProperty(Properties.DEFAULT_SUBJECT).getBytes(), SCAN_POINTER_START.getBytes(), params);
		
		System.out.println("result size: " + scanResult.getResult().size());
		
		List<Project> newProjectList = new ArrayList<Project>();
		scanResult.getResult().forEach(r -> {
			Project project = deserialize(r.getValue());
			System.out.println(project.getId());
			newProjectList.add(project);
		});
		
		System.out.println("date sort");
		
		newProjectList.stream().sorted((e1, e2) -> e1.getCreatedDate().compareTo(e2.getCreatedDate())).forEach( e -> {
			System.out.println(e.getId() + " " + e.getCreatedDate());
		});
		
		System.out.println("name sort");
		
		newProjectList.stream().sorted((e1, e2) -> e1.getName().compareTo(e2.getName())).forEach( e -> {
			System.out.println(e.getId() + " " + e.getName());
		});
		
		Project project = new Project();
		project.setName("test of hset");
		project.setCreatedById(System.getProperty(Properties.DEFAULT_SUBJECT));
		project.setCreatedDate(Date.from(Instant.now()));
		project.setDescription("hset test");
		project.setId(new ObjectId());
		project.setLastModifiedById(System.getProperty(Properties.DEFAULT_SUBJECT));
		project.setLastModifiedDate(Date.from(Instant.now()));
		project.setOwner(System.getProperty(Properties.DEFAULT_SUBJECT));
		project.setStage("new");
		
		jedis.hset(System.getProperty(Properties.DEFAULT_SUBJECT).getBytes(), Project.class.getName().concat(project.getId().toString()).getBytes(), serialize(project));
		
		params = new ScanParams();
	    params.match(Project.class.getName().concat("*"));
		
		scanResult = jedis.hscan(System.getProperty(Properties.DEFAULT_SUBJECT).getBytes(), SCAN_POINTER_START.getBytes(), params);
		
		System.out.println("result size: " + scanResult.getResult().size());
		
		jedis.close();
		mongoClient.close();
		
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