package com.nowellpoint.aws.idp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nowellpoint.aws.model.AbstractLambdaResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetTokenResponse extends AbstractLambdaResponse {
	
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
	
	@Override
	public String toString() {
		return "GetTokenResponse [token=" + token + ", getStatusCode()="
				+ getStatusCode() + ", getErrorCode()=" + getErrorCode()
				+ ", getErrorMessage()=" + getErrorMessage() + "]";
	}
}