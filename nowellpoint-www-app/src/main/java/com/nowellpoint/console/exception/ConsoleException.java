package com.nowellpoint.console.exception;

public class ConsoleException extends RuntimeException {

	private static final long serialVersionUID = 618375122517080979L;
	
	public ConsoleException() {
		super();
	}
	
	public ConsoleException(String message) {
		super(message);
	}
}