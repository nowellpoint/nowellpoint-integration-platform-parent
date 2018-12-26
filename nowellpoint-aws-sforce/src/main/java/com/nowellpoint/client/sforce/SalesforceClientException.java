package com.nowellpoint.client.sforce;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nowellpoint.client.sforce.impl.SalesforceException;
import com.nowellpoint.client.sforce.model.Error;

public class SalesforceClientException extends SalesforceException {
	
	private static final long serialVersionUID = 2726007023971974319L;
	
	/**
     * Constructs an <code>SalesforceClientException</code> with the specified status code and error.
     *
     * @param error the Salesforce API status code and Error.
     */
	
	public SalesforceClientException(int statusCode, Error error) {
		super(statusCode, error);
	}
	
	/**
     * Constructs an <code>SalesforceClientException</code> with the specified status code and list of errors.
     *
     * @param error the Salesforce API status code and array of errors.
     */
	
	public SalesforceClientException(int statusCode, ArrayNode error) {
		super(statusCode, error);
	}
	
	/**
     * Constructs an <code>SalesforceClientException</code> with the specified Salesforce API error.
     *
     * @param error the Salesforce API error and message.
     */
	
	public SalesforceClientException(SalesforceApiError error) {
		super(error.getErrorCode(), error.getMessage());
	}
}