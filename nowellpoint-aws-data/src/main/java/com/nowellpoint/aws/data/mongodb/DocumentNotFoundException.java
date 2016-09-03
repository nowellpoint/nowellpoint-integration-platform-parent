package com.nowellpoint.aws.data.mongodb;

public class DocumentNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -8014556199862076901L;
	
	public DocumentNotFoundException(String message) {
		super(message);
	}
}