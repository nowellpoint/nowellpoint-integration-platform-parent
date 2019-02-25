package com.nowellpoint.util;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
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
	
	public static String encryptBase64(String value) throws SecureValueException {
		return Base64.getEncoder().encodeToString( encrypt( value.getBytes() ) );
	}
	
	public static String decryptBase64(String base64EncodedString) throws SecureValueException {
		return new String( decrypt( Base64.getDecoder().decode( base64EncodedString ) ) );
	}

	public static byte[] encrypt(byte[] value) throws SecureValueException {	
		return doFinal(Cipher.ENCRYPT_MODE, value);
	}
	
	public static byte[] decrypt(byte[] value) throws SecureValueException {	
		return doFinal(Cipher.DECRYPT_MODE, value);
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