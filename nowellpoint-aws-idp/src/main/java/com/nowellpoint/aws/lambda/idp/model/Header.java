package com.nowellpoint.aws.lambda.idp.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Header implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1795806639590884186L;
	
	@JsonProperty("kid")
	private String keyId;
	
	@JsonProperty("alg")
	private String algorithm;
	
	public Header() {
		
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}
}