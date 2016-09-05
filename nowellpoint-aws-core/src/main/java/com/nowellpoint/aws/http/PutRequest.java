package com.nowellpoint.aws.http;

import java.net.URL;
import java.nio.charset.Charset;

public class PutRequest extends HttpRequest {

	public PutRequest(URL target) {
		super(HttpMethod.PUT, target.toString());
	}
	
	public PutRequest(String target) {
		super(HttpMethod.PUT, target);
	}
	
	public PutRequest path(String path) {
		super.path(path);
		return this;
	}
	
	public PutRequest header(String key, String value) {
		super.header(key, value);
		return this;
	}
	
	public PutRequest parameter(String key, String value) {
		super.parameter(key, value);
		return this;
	}
	
	public PutRequest parameter(String key, Boolean value) {
		super.parameter(key, value);
		return this;
	}
	
	public PutRequest acceptCharset(Charset charset) {
		super.acceptCharset(charset);
		return this;
	}
	
	public PutRequest acceptCharset(String charset) {
		super.acceptCharset(charset);
		return this;
	}
	
	public PutRequest bearerAuthorization(String bearerToken) {
		super.bearerAuthorization(bearerToken);
		return this;
	}
	
	public PutRequest contentType(String contentType) {
		super.contentType(contentType);
		return this;
	}
	
	public PutRequest accept(String accept) {
		super.accept(accept);
		return this;
	}
	
	public PutRequest basicAuthorization(String username, String password) {
		super.basicAuthorization(username, password);
		return this;
	}
	
	public PutRequest body(Object body) {
		super.body(body);
		return this;
	}
}