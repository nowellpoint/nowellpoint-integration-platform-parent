package com.nowellpoint.http;

import java.net.URL;
import java.nio.charset.Charset;

public class DeleteRequest extends HttpRequest {

	public DeleteRequest(URL endpoint) {
		super(HttpMethod.DELETE, endpoint.toString());
	}
	
	public DeleteRequest(String endpoint) {
		super(HttpMethod.DELETE, endpoint);
	}
	
	public DeleteRequest path(String path) {
		super.path(path);
		return this;
	}
	
	public DeleteRequest queryParameter(String key, String value) {
		super.queryParameter(key, value);
		return this;
	}
	
	public DeleteRequest header(String key, String value) {
		super.header(key, value);
		return this;
	}
	
	public DeleteRequest contentType(String contentType) {
		super.contentType(contentType);
		return this;
	}
	
	public DeleteRequest acceptCharset(Charset charset) {
		super.acceptCharset(charset);
		return this;
	}
	
	public DeleteRequest acceptCharset(String charset) {
		super.acceptCharset(charset);
		return this;
	}
	
	public DeleteRequest bearerAuthorization(String bearerToken) {
		super.bearerAuthorization(bearerToken);
		return this;
	}
	
	public DeleteRequest accept(String accept) {
		super.accept(accept);
		return this;
	}
	
	public DeleteRequest basicAuthorization(String username, String password) {
		super.basicAuthorization(username, password);
		return this;
	}
}