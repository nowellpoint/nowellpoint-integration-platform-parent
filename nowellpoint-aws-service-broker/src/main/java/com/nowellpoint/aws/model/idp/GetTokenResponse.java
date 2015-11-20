package com.nowellpoint.aws.model.idp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.model.AbstractResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetTokenResponse extends AbstractResponse {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -317791542723037367L;
	
	private Token token;

	public GetTokenResponse() {
		
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}
}