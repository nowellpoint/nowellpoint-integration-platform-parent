package com.nowellpoint.console.service;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.SerializationUtils;
import org.bson.types.ObjectId;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.nowellpoint.console.entity.Lead;
import com.nowellpoint.console.entity.UserProfile;
import com.nowellpoint.console.entity.Plan;
import com.nowellpoint.console.entity.Organization;
import com.nowellpoint.www.app.util.EnvironmentVariables;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public abstract class AbstractService {
	
	protected static final Datastore datastore;
	protected static final ModelMapper modelMapper;
	protected static JedisPool jedisPool;
	private static Cipher cipher;
	private static SecretKey secretKey;
	private static IvParameterSpec iv;
	
	static {
		final MongoClientURI mongoClientUri = new MongoClientURI(String.format("mongodb://%s", EnvironmentVariables.getMongoClientUri()));
		MongoClient mongoClient = new MongoClient(mongoClientUri);
        
        final Morphia morphia = new Morphia();
        
        morphia.map(Lead.class);
        morphia.map(UserProfile.class);
        morphia.map(Plan.class);
        morphia.map(Organization.class);

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
	}
	
	static {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(1000);
		
		jedisPool = new JedisPool(
				poolConfig, 
				System.getenv("REDIS_HOST"), 
				Integer.valueOf(System.getenv("REDIS_PORT")), 
				Protocol.DEFAULT_TIMEOUT, 
				System.getenv("REDIS_PASSWORD"));
		
		String keyString = System.getenv("REDIS_ENCRYPTION_KEY");
		
		try {
			byte[] key = keyString.getBytes("UTF-8");
			
			MessageDigest sha = MessageDigest.getInstance("SHA-512");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 32);
		    
		    secretKey = new SecretKeySpec(key, "AES");
		    
		    cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		    
		    byte[] ivBytes = new byte[cipher.getBlockSize()];
		    iv = new IvParameterSpec(ivBytes);
		    
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}
	
	protected <T extends Serializable> void putEntry(String key, T value) {
		Jedis jedis = jedisPool.getResource();
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			byte[] bytes = cipher.doFinal(SerializationUtils.serialize(value));
			jedis.set(key.getBytes(), bytes);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
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
				cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
				bytes = cipher.doFinal(bytes);
			} catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
				e.printStackTrace();
			}
			
			value = SerializationUtils.deserialize(bytes);
		}
		
		return value;
	}
	
	protected Date getCurrentDateTime() {
		return Date.from(Instant.now());
	}
}