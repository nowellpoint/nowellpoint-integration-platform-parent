package com.nowellpoint.util;

import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class DigitalSignature {
	
	public static String sign(String secret, String value) {
		try {
	        Mac mac = Mac.getInstance("HmacSHA256");
	        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
	        mac.init(secretKey);
	        return Base64.getEncoder().encodeToString(mac.doFinal(value.getBytes()));
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return null;
	    }
	}
}