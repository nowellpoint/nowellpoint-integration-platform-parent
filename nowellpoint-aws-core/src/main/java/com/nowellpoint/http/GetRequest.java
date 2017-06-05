package com.nowellpoint.http;

import java.net.URL;
import java.nio.charset.Charset;

public class GetRequest extends HttpRequest {

	public GetRequest(URL endpoint) {
		super(HttpMethod.GET, endpoint.toString());
	}
	
	public GetRequest(String endpoint) {
		super(HttpMethod.GET, endpoint);
	}
	
	public GetRequest path(String path) {
		super.path(path);
		return this;
	}
	
	public GetRequest queryParameter(String key, String value) {
		super.queryParameter(key, value);
		return this;
	}
	
	public GetRequest header(String key, String value) {
		super.header(key, value);
		return this;
	}
	
	public GetRequest acceptCharset(Charset charset) {
		super.acceptCharset(charset);
		return this;
	}
	
	public GetRequest acceptCharset(String charset) {
		super.acceptCharset(charset);
		return this;
	}
	
	public GetRequest bearerAuthorization(String bearerToken) {
		super.bearerAuthorization(bearerToken);
		return this;
	}
	
	public GetRequest accept(String accept) {
		super.accept(accept);
		return this;
	}
	
	public GetRequest basicAuthorization(String username, String password) {
		super.basicAuthorization(username, password);
		return this;
	}
}