package com.nowellpoint.aws.http;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class PostRequest extends HttpRequest {

	public PostRequest(URL target) {
		super(HttpMethod.POST, target.toString());
	}
	
	public PostRequest(String target) {
		super(HttpMethod.POST, target);
	}
	
	public PostRequest path(String path) {
		super.path(path);
		return this;
	}
	
	public PostRequest header(String key, String value) {
		super.header(key, value);
		return this;
	}
	
	public PostRequest parameter(String key, String value) {
		super.parameter(key, value);
		return this;
	}
	
	public PostRequest acceptCharset(Charset charset) {
		super.acceptCharset(charset);
		return this;
	}
	
	public PostRequest acceptCharset(String charset) {
		super.acceptCharset(charset);
		return this;
	}
	
	public PostRequest bearerAuthorization(String bearerToken) {
		super.bearerAuthorization(bearerToken);
		return this;
	}
	
	public PostRequest contentType(String contentType) {
		super.contentType(contentType);
		return this;
	}
	
	public PostRequest accept(String accept) {
		super.accept(accept);
		return this;
	}
	
	public PostRequest basicAuthorization(String username, String password) {
		super.basicAuthorization(username, password);
		return this;
	}
	
	public PostRequest body(String body) {
		super.parameters(URLEncodedUtils.parse(body, StandardCharsets.UTF_8)
				.stream()
				.collect(Collectors.toMap(NameValuePair::getName, (p) -> p.getValue())));
		return this;
	}
}