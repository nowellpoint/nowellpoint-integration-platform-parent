package com.nowellpoint.aws;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.util.Base64;
import org.junit.Test;

public class TestKeyManagementService {
	
	private static final AWSKMS AWSKMS_CLIENT = AWSKMSClientBuilder.defaultClient();
	
	@Test
	public void testEncryptDecrypt() {
		long startTime = System.currentTimeMillis();
		String keyId = "arn:aws:kms:us-east-1:600862814314:key/534e1894-56e5-413b-97fc-a3d6bbc0c51b";
		try {
            ByteBuffer plaintext = ByteBuffer.wrap("MY SDK ENCRYPTED value".getBytes());
            EncryptRequest req = new EncryptRequest().withKeyId(keyId).withPlaintext(plaintext);
            ByteBuffer ciphertext = AWSKMS_CLIENT.encrypt(req).getCiphertextBlob();
            
            System.out.println(System.currentTimeMillis() - startTime);

            byte[] base64EncodedValue = Base64.encode(ciphertext.array());
            String value = new String(base64EncodedValue, Charset.forName("UTF-8"));
            System.out.println("encrypted value: " + value);
            
            startTime = System.currentTimeMillis();
            
            DecryptRequest decryptRequest = new DecryptRequest().withCiphertextBlob(ByteBuffer.wrap(Base64.decode(base64EncodedValue)));
            ByteBuffer plaintextKey = AWSKMS_CLIENT.decrypt(decryptRequest).getPlaintext();
            
            byte[] key = new byte[plaintextKey.remaining()];
            plaintextKey.get(key);
            
            System.out.println(System.currentTimeMillis() - startTime);
            
            System.out.println("decrypted value: " + new String(key));
            
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon AWS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with AWS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
	}
}