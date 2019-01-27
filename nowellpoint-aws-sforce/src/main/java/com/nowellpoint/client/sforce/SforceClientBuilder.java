package com.nowellpoint.client.sforce;

import com.nowellpoint.client.sforce.model.Token;

import lombok.Builder;

class SforceClientBuilder {

	@Builder(builderMethodName = "defaultClient")
	public static Sforce defaultClient(Token token) {
		return new Sforce(token);
	}
}