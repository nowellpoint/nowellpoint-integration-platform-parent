package com.nowellpoint.aws.http;

public class HttpRequestException extends RuntimeException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 4848422950246846891L;
	
	public HttpRequestException(Exception exception) {
		super(exception);
	}
}