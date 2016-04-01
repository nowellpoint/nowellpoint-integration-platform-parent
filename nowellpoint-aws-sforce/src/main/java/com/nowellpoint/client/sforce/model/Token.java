package com.nowellpoint.client.sforce.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Token implements Serializable {
	
	private static final long serialVersionUID = -33102212500608631L;
	
	@JsonProperty(value="access_token")
	private String access_token;
	
	@JsonProperty(value="refresh_token")
	private String refresh_token;
	
	@JsonProperty(value="signature")
	private String signature;
	
	@JsonProperty(value="scope")
	private String scope;
	
	@JsonProperty(value="instance_url")
	private String instance_url;
	
	@JsonProperty(value="id")
	private String id;
	
	@JsonProperty(value="token_type")
	private String token_type;
	
	@JsonProperty(value="issued_at")
	private String issued_at;
	
	public Token() {
		
	}

	public String getAccessToken() {
		return access_token;
	}

	public void setAccessToken(String access_token) {
		this.access_token = access_token;
	}

	public String getRefreshToken() {
		return refresh_token;
	}

	public void setRefreshToken(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getInstanceUrl() {
		return instance_url;
	}

	public void setInstanceUrl(String instance_url) {
		this.instance_url = instance_url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTokenType() {
		return token_type;
	}

	public void setTokenType(String token_type) {
		this.token_type = token_type;
	}

	public String getIssuedAt() {
		return issued_at;
	}

	public void setIssuedAt(String issued_at) {
		this.issued_at = issued_at;
	}
}