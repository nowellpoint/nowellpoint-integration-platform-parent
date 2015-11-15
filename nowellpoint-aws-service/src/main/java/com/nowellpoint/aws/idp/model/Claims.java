package com.nowellpoint.aws.idp.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Claims implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 8576239688810821366L;
	
	@JsonProperty(value="jti")
	private String tokenId;
	
	@JsonProperty(value="iat")
	private Long issuedAt;
	
	@JsonProperty(value="iss")
	private String issuer;
	
	@JsonProperty(value="sub")
	private String subject;
	
	@JsonProperty(value="exp")
	private Long expirationTime;
	
	@JsonProperty(value="rti")
	private String refreshTokenId;
	
	public Claims() {
		
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public Long getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(Long issuedAt) {
		this.issuedAt = issuedAt;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Long getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Long expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getRefreshTokenId() {
		return refreshTokenId;
	}

	public void setRefreshTokenId(String refreshTokenId) {
		this.refreshTokenId = refreshTokenId;
	}
}