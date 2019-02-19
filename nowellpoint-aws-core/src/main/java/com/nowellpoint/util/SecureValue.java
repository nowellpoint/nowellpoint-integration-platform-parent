package com.nowellpoint.util;

import java.security.MessageDigest;
import java.util.Arrays;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SecureValue {
	
	private static final String keyString = SecretsManager.getRedisEncryptionKey();

	public static byte[] encrypt(byte[] bytes) throws SecureValueException {	
		return doFinal(Cipher.ENCRYPT_MODE, bytes);
	}
	
	public static byte[] decrypt(byte[] bytes) throws SecureValueException {	
		return doFinal(Cipher.DECRYPT_MODE, bytes);
	}
	
	private static byte[] doFinal(int cipherMode, byte[] bytes) throws SecureValueException {
		try {
			byte[] key = keyString.getBytes("UTF-8");
			
			MessageDigest sha = MessageDigest.getInstance("SHA-512");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 32);
		    
			SecretKey secretKey = new SecretKeySpec(key, "AES");
		    
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			
			IvParameterSpec spec = new IvParameterSpec(new byte[cipher.getBlockSize()]);
			
			cipher.init(cipherMode, secretKey, spec);
			
			return cipher.doFinal(bytes);
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			throw new SecureValueException(e);
		}
	}
}