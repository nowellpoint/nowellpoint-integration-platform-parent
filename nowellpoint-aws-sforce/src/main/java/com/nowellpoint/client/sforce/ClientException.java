package com.nowellpoint.client.sforce;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nowellpoint.client.sforce.impl.SalesforceException;
import com.nowellpoint.client.sforce.model.Error;

public class ClientException extends SalesforceException {
	
	private static final long serialVersionUID = 2726007023971974319L;
	
	public ClientException(int statusCode, Error error) {
		super(statusCode, error);
	}
	
	public ClientException(int statusCode, ObjectNode error) {
		super(statusCode, error);
	}
}