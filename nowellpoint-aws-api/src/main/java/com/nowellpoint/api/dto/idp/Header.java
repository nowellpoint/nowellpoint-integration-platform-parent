package com.nowellpoint.api.dto.idp;

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
	private String kid;
	
	@JsonProperty("alg")
	private String alg;
	
	public Header() {
		
	}

	public String getKeyId() {
		return kid;
	}

	public void setKeyId(String keyId) {
		this.kid = keyId;
	}

	public String getAlgorithm() {
		return alg;
	}

	public void setAlgorithm(String algorithm) {
		this.alg = algorithm;
	}
}