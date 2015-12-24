package com.nowellpoint.aws.model;

import java.util.Base64;

public abstract class Base64EncoderDecoder {

	protected String decode(String value) {
		return new String(Base64.getDecoder().decode(value));
	}
	
	protected String encode(String value) {
		return Base64.getEncoder().encodeToString(value.getBytes());
	}
}