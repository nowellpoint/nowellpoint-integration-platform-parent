package com.nowellpoint.api.dto.idp;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpandedJwt implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1985891022164156714L;
	
	private Header header;
	
	private Claims claims;
	
	private String signature;
	
	public ExpandedJwt() {
		
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public Claims getClaims() {
		return claims;
	}

	public void setClaims(Claims claims) {
		this.claims = claims;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}