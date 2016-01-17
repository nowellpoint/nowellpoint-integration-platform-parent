package com.nowellpoint.aws.model.sforce;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;

import com.nowellpoint.aws.model.AbstractLambdaRequest;

public class GetTokenRequest extends AbstractLambdaRequest implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 3543119199096532676L;
	
	private String tokenUri;
	
	private String clientId;
	
	private String clientSecret;
	
	private String username;
	
	private String password;
	
	private String securityToken;

	public GetTokenRequest() {
		
	}

	@NotEmpty
	public String getTokenUri() {
		return tokenUri;
	}

	public void setTokenUri(String tokenUri) {
		this.tokenUri = tokenUri;
	}

	@NotEmpty
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@NotEmpty
	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	@NotEmpty
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@NotEmpty
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@NotEmpty
	public String getSecurityToken() {
		return securityToken;
	}

	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}
	
	public GetTokenRequest withTokenUri(String tokenUri) {
		setTokenUri(tokenUri);
		return this;
	}
	
	public GetTokenRequest withClientId(String clientId) {
		setClientId(clientId);
		return this;
	}
	
	public GetTokenRequest withClientSecret(String clientSecret) {
		setClientSecret(clientSecret);
		return this;
	}
	
	public GetTokenRequest withUsername(String username) {
		setUsername(username);
		return this;
	}
	
	public GetTokenRequest withPassword(String password) {
		setPassword(password);
		return this;
	}

	public GetTokenRequest withSecurityToken(String securityToken) {
		setSecurityToken(securityToken);
		return this;
	}
}