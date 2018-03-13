package com.nowellpoint.content.model;

public class Braintree {
	
	private String environment;
	private String merchantId;
	private String publicKey;
	private String privateKey;
	
	public Braintree() {
		
	}

	public String getEnvironment() {
		return environment;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}
}