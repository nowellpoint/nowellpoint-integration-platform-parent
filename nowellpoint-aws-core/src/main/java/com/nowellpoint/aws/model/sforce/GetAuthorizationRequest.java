package com.nowellpoint.aws.model.sforce;

import java.io.Serializable;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public class GetAuthorizationRequest extends AbstractLambdaRequest implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 8458926047232428590L;
	
	private String code;
	
	private String tokenUri;
	
	private String clientId;
	
	private String clientSecret;
	
	private String redirectUri;

	public GetAuthorizationRequest() {
		
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getTokenUri() {
		return tokenUri;
	}

	public void setTokenUri(String tokenUri) {
		this.tokenUri = tokenUri;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public GetAuthorizationRequest withCode(String code) {
		setCode(code);
		return this;
	}
	
	public GetAuthorizationRequest withTokenUri(String tokenUri) {
		setTokenUri(tokenUri);
		return this;
	}
	
	public GetAuthorizationRequest withClientId(String clientId) {
		setClientId(clientId);
		return this;
	}
	
	public GetAuthorizationRequest withClientSecret(String clientSecret) {
		setClientSecret(clientSecret);
		return this;
	}
	
	public GetAuthorizationRequest withRedirectUri(String redirectUri) {
		setRedirectUri(redirectUri);
		return this;
	}
}