package com.nowellpoint.client.resource;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.nowellpoint.aws.idp.model.Token;

public class AbstractResource {
	
	protected static final String API_ENDPOINT = System.getenv("NCS_API_ENDPOINT");
	protected static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
	
	protected Token token;
	
	public AbstractResource(Token token) {
		this.token = token;
	}
}