package com.nowellpoint.aws.idp.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.model.AbstractResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetTokenResponse extends AbstractResponse implements Serializable {
	
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