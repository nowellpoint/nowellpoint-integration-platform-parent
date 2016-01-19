package com.nowellpoint.aws.http;

import java.net.URL;

public class RestResource {
	
	private RestResource() {
		
	}
	
	public static GetRequest get(URL target) {
		return new GetRequest(target);
	}
	
	public static GetRequest get(String target) {
		return new GetRequest(target);
	}
	
	public static PostRequest post(URL target) {
		return new PostRequest(target);
	}
	
	public static PostRequest post(String target) {
		return new PostRequest(target);
	}
	
	public static DeleteRequest delete(URL target) {
		return new DeleteRequest(target);
	}
	
	public static DeleteRequest delete(String target) {
		return new DeleteRequest(target);
	}
}