package com.nowellpoint.client.sforce;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nowellpoint.client.sforce.model.Error;

public class SalesforceClientException extends RuntimeException {
	
	private static final long serialVersionUID = 2726007023971974319L;
	
	/**
     * Constructs an <code>SalesforceClientException</code> with the specified status code and error.
     *
     * @param error the Salesforce API status code and Error.
     */
	
	public SalesforceClientException(int statusCode, Error error) {
		super(error.getErrorDescription());
	}
	
	/**
     * Constructs an <code>SalesforceClientException</code> with the specified status code and list of errors.
     *
     * @param error the Salesforce API status code and array of errors.
     */
	
	public SalesforceClientException(int statusCode, ArrayNode error) {
		super(error.get("errorMessage").asText());
	}
	
	/**
     * Constructs an <code>SalesforceClientException</code> with the specified Salesforce API error.
     *
     * @param error the Salesforce API error and message.
     */
	
	public SalesforceClientException(ApiError error) {
		super(error.getMessage());
	}
	
	public SalesforceClientException(String message) {
		super(message);
	}
}