package com.nowellpoint.aws.model;

public class ClientException extends RuntimeException {

	/**
	 * 
	 */

	private static final long serialVersionUID = -1428665704644815400L;
	
	public ClientException(Exception exception) {
		super(exception);
	}
	
	public ClientException(String message) {
		super(message);
	}
}