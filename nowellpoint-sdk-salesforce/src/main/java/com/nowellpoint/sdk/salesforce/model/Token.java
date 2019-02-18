package com.nowellpoint.sdk.salesforce.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Token implements Serializable {
	
	private static final long serialVersionUID = -33102212500608631L;
	
	@JsonProperty(value="access_token")
	private String accessToken;
	
	@JsonProperty(value="refresh_token")
	private String refreshToken;
	
	@JsonProperty(value="signature")
	private String signature;
	
	@JsonProperty(value="scope")
	private String scope;
	
	@JsonProperty(value="instance_url")
	private String instanceUrl;
	
	@JsonProperty(value="id")
	private String id;
	
	@JsonProperty(value="token_type")
	private String tokenType;
	
	@JsonProperty(value="issued_at")
	private String issuedAt;
	
	public Token() {
		
	}
	
	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getSignature() {
		return signature;
	}

	public String getScope() {
		return scope;
	}

	public String getInstanceUrl() {
		return instanceUrl;
	}

	public String getId() {
		return id;
	}

	public String getTokenType() {
		return tokenType;
	}

	public String getIssuedAt() {
		return issuedAt;
	}
}