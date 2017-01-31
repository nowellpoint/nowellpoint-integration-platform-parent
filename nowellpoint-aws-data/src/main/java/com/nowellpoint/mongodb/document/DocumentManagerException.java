package com.nowellpoint.mongodb.document;

public class DocumentManagerException extends RuntimeException {
	
	private static final long serialVersionUID = -8166177562842441161L;
	
	public DocumentManagerException() {
		super();
	}
	
	public DocumentManagerException(String message) {
		super(message);
	}
	
	public DocumentManagerException(Throwable cause) {
		super(cause);
	}
}