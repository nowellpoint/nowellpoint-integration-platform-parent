package com.nowellpoint.kms.test;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

import com.amazonaws.util.Base64;
import com.nowellpoint.util.SecretsManager;

public class TestEncryptDecryptKey {
	
	private static Cipher cipher;
	private static SecretKey secretKey;
	private static IvParameterSpec iv;

	@Test
	public void testEncryptDecryptKey() {
		
		String token = "5Aep8619juAXTkx27YWQC4qXSEFzysxOhJ6OZXq1v_n7fHJC2.7kH5RQ36kfM57wFXLMVhGVJtA4kEYCBEs6T2x";
		
		long start = System.currentTimeMillis();
	    
	    String keyString = SecretsManager.getRedisEncryptionKey();
	    
	    start = System.currentTimeMillis();
		
		try {
			byte[] key = keyString.getBytes("UTF-8");
			
			MessageDigest sha = MessageDigest.getInstance("SHA-512");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 32);
		    
		    secretKey = new SecretKeySpec(key, "AES");
		    
		    cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		    
		    iv = new IvParameterSpec(new byte[cipher.getBlockSize()]);
		    
		    cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			byte[] bytes = cipher.doFinal(token.getBytes());
			
			String s = Base64.encodeAsString( bytes );
			
			System.out.println(s);
			
			cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
			bytes = cipher.doFinal(Base64.decode(s));
			
			s = new String( bytes );
			
			assertEquals(token, s);
			
			System.out.println(s);
			
			System.out.println(System.currentTimeMillis() - start);
		    
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
	}
}