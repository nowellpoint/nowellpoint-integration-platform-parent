package com.nowellpoint.client.model.idp;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Claims implements Serializable {
	
	private static final long serialVersionUID = 8576239688810821366L;
	
	@JsonProperty(value="jti")
	private String jti;
	
	@JsonProperty(value="iat")
	private Long iat;
	
	@JsonProperty(value="iss")
	private String iss;
	
	@JsonProperty(value="sub")
	private String sub;
	
	@JsonProperty(value="exp")
	private Long exp;
	
	@JsonProperty(value="rti")
	private String rti;
	
	public Claims() {
		
	}

	public String getTokenId() {
		return jti;
	}

	public void setTokenId(String tokenId) {
		this.jti = tokenId;
	}

	public Long getIssuedAt() {
		return iat;
	}

	public void setIssuedAt(Long issuedAt) {
		this.iat = issuedAt;
	}

	public String getIssuer() {
		return iss;
	}

	public void setIssuer(String issuer) {
		this.iss = issuer;
	}

	public String getSubject() {
		return sub;
	}

	public void setSubject(String subject) {
		this.sub = subject;
	}

	public Long getExpirationTime() {
		return exp;
	}

	public void setExpirationTime(Long expirationTime) {
		this.exp = expirationTime;
	}

	public String getRefreshTokenId() {
		return rti;
	}

	public void setRefreshTokenId(String refreshTokenId) {
		this.rti = refreshTokenId;
	}
}