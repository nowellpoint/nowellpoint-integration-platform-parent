package com.nowellpoint.client.sforce;

import com.nowellpoint.client.sforce.model.Token;

import lombok.Builder;

public class SalesforceClientBuilder {

	@Builder(builderMethodName = "defaultClient")
	public static Salesforce defaultClient(Token token) {
		return new SalesforceClient(token);
	}
}