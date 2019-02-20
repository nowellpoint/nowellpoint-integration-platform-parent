package com.nowellpoint.kms.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.nowellpoint.util.SecureValue;
import com.nowellpoint.util.SecureValueException;

public class TestEncryptDecryptKey {

	@Test
	public void testEncryptDecryptKey() {
		
		String token = "5Aep8619juAXTkx27YWQC4qXSEFzysxOhJ6OZXq1v_n7fHJC2.7kH5RQ36kfM57wFXLMVhGVJtA4kEYCBEs6T2x";
		
		String s = null;
		try {
			s = SecureValue.encryptBase64(token);
			s = SecureValue.decryptBase64(s);
		} catch (SecureValueException e) {
			e.printStackTrace();
		}
		
		assertEquals(token, s);
	}
}