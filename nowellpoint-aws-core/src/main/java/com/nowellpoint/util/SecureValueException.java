package com.nowellpoint.util;

public class SecureValueException extends RuntimeException {

	private static final long serialVersionUID = -1408512973353507974L;
	
	public SecureValueException(Exception e) {
		super(e);
	}
}