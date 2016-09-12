package com.nowellpoint.client;

import com.nowellpoint.aws.idp.model.Token;

public class AbstractResource {
	
	protected static final String API_ENDPOINT = System.getenv("NCS_API_ENDPOINT");
	
	protected Token token;
	
	public AbstractResource(Token token) {
		this.token = token;
	}

}
