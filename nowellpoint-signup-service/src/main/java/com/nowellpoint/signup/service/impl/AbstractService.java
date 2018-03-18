package com.nowellpoint.signup.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;

import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nowellpoint.signup.entity.UserProfile;
import com.nowellpoint.signup.model.ModifiableUserInfo;
import com.nowellpoint.signup.model.UserInfo;
import com.nowellpoint.signup.provider.CacheProvider;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

public abstract class AbstractService {
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	protected static final ModelMapper modelMapper = new ModelMapper();
	
	static {
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
		modelMapper.addConverter(new AbstractConverter<UserProfile, UserInfo>() {
			@Override
			protected UserInfo convert(UserProfile source) {
				return source == null ? null : modelMapper.map(source, ModifiableUserInfo.class).toImmutable();
			}
		});
		
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	@Inject
	private CacheProvider cacheProvider;
	
	private static Cipher cipher;
	private static SecretKey secretKey;
	private static IvParameterSpec iv;
	
	public AbstractService() {
		String keyString = System.getenv("CACHE_DATA_ENCRYPTION_KEY");
		
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
	
	private Jedis getCache() {
		return cacheProvider.getCache();
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	
	public <T> void set(String key, T value) {
		Jedis jedis = getCache();
		try {
			jedis.set(key.getBytes(), serialize(value));
		} finally {
			jedis.close();
		}
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
	
	public <T> Set<T> smembers(Class<T> type, String key) {
		Jedis jedis = getCache();
		Set<T> results = new HashSet<T>();
		
		try {
			jedis.smembers(key.getBytes()).stream().forEach(m -> {
				results.add(deserialize(m,type));
			});
		} finally {
			jedis.close();
		}
		
		return results;
	}
	
	/**
	 * 
	 * @param type
	 * @param key
	 * @return
	 */
	
	public <T> T get(Class<T> type, String key) {
		Jedis jedis = getCache();
		byte[] bytes = null;
		
		try {
			bytes = jedis.get(key.getBytes());
		} finally {
			jedis.close();
		}
		
		T value = null;
		if (bytes != null) {
			value = deserialize(bytes, type);
		}
		
		return value;
	}
	
	/**
	 * 
	 * @param key
	 * @param seconds
	 * @param value
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
	
	public <T> Set<T> hgetAll(Class<T> type, String key) {
		Jedis jedis = getCache();
		Set<T> results = new HashSet<T>();
		try {
			jedis.hgetAll(key.getBytes()).values().stream().forEach(m -> {
				try {
					results.add(deserialize(m, type));
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
	
	public <T> T hget(Class<T> type, String key, String field) {
		Jedis jedis = getCache();
		byte[] bytes = null;
		
		try {
			bytes = jedis.hget(key.getBytes(), field.getBytes());
		} finally {
			jedis.close();
		}
		
		T value = null;
		if (bytes != null) {
			value = deserialize(bytes, type);
		}
		
		return value;
	}
	
	/**
	 * 
	 * @param object
	 * @return
	 */
	
	public byte[] serialize(Object object) {
		byte[] bytes = null;
		try {
			String json = Base64.getEncoder().encodeToString(objectMapper.writeValueAsString(object).getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			bytes = cipher.doFinal(json.getBytes("UTF8"));
		} catch (JsonProcessingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			//logger.severe("Cache serialize issue >>>");
			e.printStackTrace();
		}
        
		return bytes;
    }
	
	/**
	 * 
	 * @param bytes
	 * @param type
	 * @return
	 */
	
	public <T> T deserialize(byte[] bytes, Class<T> type) {
		T object = null;
		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
			bytes = cipher.doFinal(bytes);
			object = objectMapper.readValue(Base64.getDecoder().decode(bytes), type);
		} catch (IOException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			//logger.severe("Cache deserialize issue >>>");
			e.printStackTrace();
		}
		
		return object;
	}
}