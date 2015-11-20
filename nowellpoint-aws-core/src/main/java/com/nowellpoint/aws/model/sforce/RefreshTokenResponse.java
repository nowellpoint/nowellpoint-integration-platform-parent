package com.nowellpoint.aws.model.sforce;

import com.nowellpoint.aws.model.AbstractResponse;

public class RefreshTokenResponse extends AbstractResponse {

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