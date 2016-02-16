package com.nowellpoint.www.app.client;

public class IdentityProviderException extends RuntimeException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2914090531128618246L;
	
	public IdentityProviderException(Exception exception) {
		super(exception);
	}
	
	public IdentityProviderException(String message) {
		super(message);
	}
}