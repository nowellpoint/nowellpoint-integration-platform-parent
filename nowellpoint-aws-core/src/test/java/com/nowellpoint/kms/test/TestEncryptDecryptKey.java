package com.nowellpoint.kms.test;

import java.nio.ByteBuffer;
import java.util.Base64;

import org.junit.Test;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.EncryptRequest;

public class TestEncryptDecryptKey {

	@Test
	public void testEncryptDecryptKey() {
		
		String keyId = "534e1894-56e5-413b-97fc-a3d6bbc0c51b";
		
		AWSKMS kmsClient = AWSKMSClientBuilder.defaultClient();
		
		ByteBuffer plainText = ByteBuffer.wrap(Base64.getDecoder().decode("littleredridinghood"));
	    EncryptRequest req = new EncryptRequest().withKeyId(keyId)
	            .withPlaintext(plainText);
	    
	    ByteBuffer encryptedText = kmsClient.encrypt(req).getCiphertextBlob();
	    System.out.println( new String(encryptedText.array()) );
	}
}