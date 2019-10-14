package com.nowellpoint.client.sforce;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.nowellpoint.client.sforce.model.Error;

public class SalesforceClientException extends RuntimeException {
	
	private static final long serialVersionUID = 2726007023971974319L;
	
	private int statusCode;
	private String errorCode;
	private String[] fields;
	private Error[] errors;
	
	/**
     * Constructs an <code>SalesforceClientException</code> with the specified status code and error.
     *
     * @param error the Salesforce API status code and Error.
     */
	
	public SalesforceClientException(int statusCode, Error error) {
		super(parseError(error));
		this.statusCode = statusCode;
		this.errors = new Error[] {error};
	}
	
	/**
     * Constructs an <code>SalesforceClientException</code> with the specified status code and array of errors.
     *
     * @param error the Salesforce API status code and array of errors.
     */
	
	public SalesforceClientException(int statusCode, Error[] errors) {
		super(Arrays.asList(errors).stream()
				.map(error -> parseError(error))
				.collect(Collectors.joining(", ")));
		this.statusCode = statusCode;
		this.errors = errors;
	}
	
	/**
     * Constructs an <code>SalesforceClientException</code> with the specified Salesforce API error.
     *
     * @param error the Salesforce API error and message.
     */
	
	public SalesforceClientException(int statusCode, ApiError error) {
		super(error.getMessage());
		this.statusCode = statusCode;
		this.errorCode = error.getErrorCode();
		this.fields = error.getFields();
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public String[] getFields() {
		return fields;
	}
	
	public Error[] getErrors() {
		return errors;
	}
	
	private static String parseError(Error error) {
		return error.getErrorDescription() != null ? error.getErrorDescription() : error.getMessage();
	}
}