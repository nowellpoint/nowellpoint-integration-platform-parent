package com.nowellpoint.aws.model.sforce;

import com.nowellpoint.aws.model.AbstractLambdaResponse;

public class RefreshTokenResponse extends AbstractLambdaResponse {

	private static final long serialVersionUID = -1681769829455661159L;
	
	private Token token;
	
	public RefreshTokenResponse() {
		
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}
}