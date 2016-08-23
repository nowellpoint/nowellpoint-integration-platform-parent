package com.nowellpoint.client.sforce.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nowellpoint.client.sforce.model.Error;

public class SalesforceException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	private int statusCode;
	
	private String error;
	
	private String errorDescription;

	public SalesforceException(int statusCode, Error error) {
		super();
		this.statusCode = statusCode;
		this.error = error.getError();
		this.errorDescription = error.getErrorDescription();
	}
	
	public SalesforceException(int statusCode, ArrayNode node) {
		//[{"message":"TotalRequests Limit exceeded.","errorCode":"REQUEST_LIMIT_EXCEEDED"}]
		super();
		this.statusCode = statusCode;
		this.error = node.get(0).get("errorCode").asText();
		this.errorDescription = node.get(0).get("message").asText();
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getError() {
		return error;
	}

	public String getErrorDescription() {
		return errorDescription;
	}

}
