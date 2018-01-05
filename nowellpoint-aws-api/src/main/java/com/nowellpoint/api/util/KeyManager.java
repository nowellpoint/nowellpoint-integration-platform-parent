package com.nowellpoint.api.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.jboss.logging.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.util.Base64;

public class KeyManager {
	
	private static final Logger LOGGER = Logger.getLogger(KeyManager.class);
	private static final AWSKMS AWSKMS_CLIENT = AWSKMSClientBuilder.defaultClient();
	private static final String KEY_ID = "arn:aws:kms:us-east-1:600862814314:key/534e1894-56e5-413b-97fc-a3d6bbc0c51b";

	public static String encrypt(String value) {
		try {
            ByteBuffer plaintext = ByteBuffer.wrap(value.getBytes());
            EncryptRequest request = new EncryptRequest().withKeyId(KEY_ID).withPlaintext(plaintext);
            ByteBuffer ciphertext = AWSKMS_CLIENT.encrypt(request).getCiphertextBlob();

            byte[] base64EncodedValue = Base64.encode(ciphertext.array());
            return new String(base64EncodedValue, Charset.forName("UTF-8"));
            
        } catch (AmazonServiceException e) {
        		LOGGER.error(e);
        } catch (AmazonClientException e) {
            LOGGER.error(e);
        }
		
		return null;
	}
	
	public static String decrypt(String value) {
		try {
            DecryptRequest decryptRequest = new DecryptRequest().withCiphertextBlob(ByteBuffer.wrap(Base64.decode(value)));
            ByteBuffer plaintextKey = AWSKMS_CLIENT.decrypt(decryptRequest).getPlaintext();
            
            byte[] key = new byte[plaintextKey.remaining()];
            plaintextKey.get(key);
          
            return new String(key);
            
        } catch (AmazonServiceException e) {
        		LOGGER.error(e);
        } catch (AmazonClientException e) {
        		LOGGER.error(e);
        }
		
		return null;
	}
}