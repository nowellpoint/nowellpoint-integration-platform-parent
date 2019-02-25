package com.nowellpoint.console.service;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.commons.lang3.SerializationUtils;
import org.bson.types.ObjectId;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.console.entity.Identity;
import com.nowellpoint.console.entity.Lead;
import com.nowellpoint.console.entity.Organization;
import com.nowellpoint.console.entity.Plan;
import com.nowellpoint.console.entity.StreamingEvent;
import com.nowellpoint.console.model.OrganizationInfo;
import com.nowellpoint.console.model.UserInfo;
import com.nowellpoint.util.SecretsManager;
import com.nowellpoint.util.SecureValue;
import com.nowellpoint.util.SecureValueException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public abstract class AbstractService {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractService.class.getName());
	private static final MongoClientURI mongoClientUri;
	protected static final Datastore datastore;
	protected static final ModelMapper modelMapper;
	protected static JedisPool jedisPool;
	
	static {
		mongoClientUri = new MongoClientURI(String.format("mongodb://%s", SecretsManager.getMongoClientUri()));
		MongoClient mongoClient = new MongoClient(mongoClientUri);
        
        final Morphia morphia = new Morphia();
        
        morphia.map(Lead.class);
        morphia.map(Identity.class);
        morphia.map(Plan.class);
        morphia.map(Organization.class);
        morphia.map(StreamingEvent.class);

        datastore = morphia.createDatastore(mongoClient, mongoClientUri.getDatabase());
        datastore.ensureIndexes();
	}
	
	static {
		modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		modelMapper.getConfiguration().setMethodAccessLevel(AccessLevel.PRIVATE);
		modelMapper.getConfiguration().setFieldMatchingEnabled(true);
		modelMapper.getConfiguration().setFieldAccessLevel(AccessLevel.PRIVATE);
		modelMapper.addConverter(new AbstractConverter<String, ObjectId>() {
			@Override
			protected ObjectId convert(String source) {
				return source == null ? null : new ObjectId(source);
			}
		});
		modelMapper.addConverter(new AbstractConverter<ObjectId, String>() {		
			@Override
			protected String convert(ObjectId source) {
				return source == null ? null : source.toString();
			}
		});
		modelMapper.addConverter(new AbstractConverter<UserInfo, com.nowellpoint.console.entity.Identity>() {		
			@Override
			protected com.nowellpoint.console.entity.Identity convert(UserInfo source) {
				return source == null ? null : new com.nowellpoint.console.entity.Identity(source.getId());
			}
		});
		modelMapper.addConverter(new AbstractConverter<OrganizationInfo, com.nowellpoint.console.entity.Organization>() {		
			@Override
			protected com.nowellpoint.console.entity.Organization convert(OrganizationInfo source) {
				return source == null ? null : new com.nowellpoint.console.entity.Organization(source.getId());
			}
		});
	}
	
	static {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(1000);
		
		jedisPool = new JedisPool(
				poolConfig, 
				SecretsManager.getRedisHost(), 
				Integer.valueOf(SecretsManager.getRedisPort()), 
				Protocol.DEFAULT_TIMEOUT, 
				SecretsManager.getRedisPassword());
		
		jedisPool.getResource().flushDB();
	}
	
	protected <T extends Serializable> void putEntry(String key, T value) {
		Jedis jedis = jedisPool.getResource();
		try {
			byte[] bytes = SecureValue.encrypt(SerializationUtils.serialize(value));
			jedis.set(key.getBytes(), bytes);
		} catch (SecureValueException e) {
			LOGGER.severe(ExceptionUtils.getStackTrace(e));
		} finally {
			jedis.close();
		}
	}
	
	protected <T extends Serializable> T getEntry(String key) {		
		Jedis jedis = jedisPool.getResource();
		byte[] bytes = null;
		
		try {
			bytes = jedis.get(key.getBytes());
		} finally {
			jedis.close();
		}
		
		T value = null;
		
		if (bytes != null) {
			try {
				bytes = SecureValue.decrypt(bytes);
				value = SerializationUtils.deserialize(bytes);
			} catch (SecureValueException e) {
				LOGGER.severe(ExceptionUtils.getStackTrace(e));
			}
		}
		
		return value;
	}
	
	protected <T extends Serializable> void removeEntry(String key) {
		Jedis jedis = jedisPool.getResource();
		
		try {
			jedis.del(key.getBytes());
		} finally {
			jedis.close();
		}
	}
	
	protected Date getCurrentDateTime() {
		return Date.from(Instant.now());
	}
	
	protected Identity getSystemAdmin() {
		Query<Identity> query = datastore.createQuery(Identity.class)
				.field("username")
				.equal("system.administrator@nowellpoint.com");
				 
		return query.get();
	}
}