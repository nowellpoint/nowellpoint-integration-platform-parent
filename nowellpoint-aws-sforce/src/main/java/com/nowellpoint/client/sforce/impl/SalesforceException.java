package com.nowellpoint.client.sforce.impl;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nowellpoint.client.sforce.model.Error;

public class SalesforceException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	private int statusCode;
	
	private String errorCode;
	
	private String message;
	
	private String errorDescription;

	public SalesforceException(int statusCode, Error error) {
		super(error.getErrorCode().concat(": ").concat(error.getMessage()));
		this.statusCode = statusCode;
		this.message = error.getMessage();
		this.errorCode = error.getErrorCode();
		this.errorDescription = error.getErrorDescription();
	}
	
	public SalesforceException(int statusCode, ArrayNode node) {
		super(node.get(0).get("errorCode").asText().concat(": ").concat(node.get(0).get("message").asText()));
		this.statusCode = statusCode;
		this.message = node.get(0).get("message").asText();
		this.errorCode = node.get(0).get("errorCode").asText();
		this.errorDescription = node.get(0).get("message").asText();
	}
	
	public SalesforceException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getMessage() {
		return message;
	}

	public String getErrorDescription() {
		return errorDescription;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
}