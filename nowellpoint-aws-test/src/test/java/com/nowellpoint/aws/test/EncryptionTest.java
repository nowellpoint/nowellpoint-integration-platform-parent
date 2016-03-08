package com.nowellpoint.aws.test;

import static org.junit.Assert.assertEquals;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

public class EncryptionTest {
	
	private static Cipher cipher = null;
	
	@Test
	public void testEncryption() {
		
		try {
			String keyString = "C0BAE23DF8B51807B3E17D21925FADF273A70181E1D81B8EDE6C76A5C1F1716E";
			
			byte[] key = keyString.getBytes("UTF-8");
			MessageDigest sha = MessageDigest.getInstance("SHA-512");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 32);
		    
		    SecretKey secretKey = new SecretKeySpec(key, "AES");
		    
		    cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			
			byte[] ivBytes = new byte[cipher.getBlockSize()];
		    IvParameterSpec iv = new IvParameterSpec(ivBytes);

			String plainText = "Java Cryptography Extension";
			System.out.println("Plain Text Before Encryption: " + plainText);

			byte[] plainTextByte = plainText.getBytes("UTF8");
			byte[] encryptedBytes = encrypt(plainTextByte, secretKey, iv);

			String encryptedText = new String(encryptedBytes, "UTF8");
			System.out.println("Encrypted Text After Encryption: " + encryptedText);

			byte[] decryptedBytes = decrypt(encryptedBytes, secretKey, iv);
			String decryptedText = new String(decryptedBytes, "UTF8");
			System.out.println("Decrypted Text After Decryption: " + decryptedText);
			
			assertEquals(plainText, decryptedText);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	static byte[] encrypt(byte[] plainTextByte, SecretKey secretKey, IvParameterSpec iv) throws Exception {
		long start = System.currentTimeMillis();
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
		byte[] encryptedBytes = cipher.doFinal(plainTextByte);
		System.out.println(System.currentTimeMillis() - start);
		return encryptedBytes;
	}
	
	static byte[] decrypt(byte[] encryptedBytes, SecretKey secretKey, IvParameterSpec iv) throws Exception {
		long start = System.currentTimeMillis();
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		System.out.println(System.currentTimeMillis() - start);
		return decryptedBytes;
	}
}