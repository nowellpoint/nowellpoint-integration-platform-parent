package com.nowellpoint.console.exception;

import com.nowellpoint.console.model.SalesforceApiError;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 618375122517080979L;
	
	private String errorCode;
	
	/**
     * Constructs an <code>ServiceException</code> with no detail message.
     */
	
	public ServiceException() {
		super();
	}
	
	/**
     * Constructs an <code>ServiceException</code> with the specified detail message.
     *
     * @param message the detail message.
     */
	
	public ServiceException(String message) {
		super(message);
	}
	
	/**
     * Constructs an <code>ServiceException</code> with the specified cause.
     *
     * @param cause the detail message.
     */
	
	public ServiceException(Throwable cause) {
		super(cause);
	}
	
	/**
     * Constructs an <code>ServiceException</code> with the specified detail message
     * and cause.
     *
     * @param message the detail message.
     * @param cause the detail message.
     */
	
	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/**
     * Constructs an <code>ServiceException</code> with the specified cause.
     *
     * @param error the Salesforce API error and error description.
     */
	
	public ServiceException(SalesforceApiError error) {
		super(error.getMessage());
		this.errorCode = error.getErrorCode();
	}
	
	/**
	 * Returns the error code associated with <code>ServiceException</code>
	 * 
	 * @return the Salesforce api errorCode
	 */
	
	public String getErrorCode() {
		return errorCode;
	}
}