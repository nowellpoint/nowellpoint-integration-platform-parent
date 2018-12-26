package com.nowellpoint.client.sforce;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nowellpoint.client.sforce.impl.SalesforceException;
import com.nowellpoint.client.sforce.model.Error;

public class SalesforceClientException extends SalesforceException {
	
	private static final long serialVersionUID = 2726007023971974319L;
	
	public SalesforceClientException(int statusCode, Error error) {
		super(statusCode, error);
	}
	
	public SalesforceClientException(int statusCode, ArrayNode error) {
		super(statusCode, error);
	}
	
	/**
     * Constructs an <code>SalesforceClientException</code> with the specified cause.
     *
     * @param error the Salesforce API error and error description.
     */
	
	public SalesforceClientException(SalesforceApiError error) {
		super(error.getErrorCode(), error.getMessage());
	}
}