package com.nowellpoint.client;

import java.text.SimpleDateFormat;

import com.nowellpoint.aws.idp.model.Token;

public class AbstractResource {
	
	protected static final String API_ENDPOINT = System.getenv("NCS_API_ENDPOINT");
	protected static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	protected static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	
	protected Token token;
	
	public AbstractResource(Token token) {
		this.token = token;
	}

}
