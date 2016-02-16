package com.nowellpoint.www.app.client;

public class AuthenticationException extends Exception {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 6668475177045862900L;
	
	public AuthenticationException(Exception exception) {
		super(exception);
	}

	public AuthenticationException(String message) {
		super(message);
	}
}
