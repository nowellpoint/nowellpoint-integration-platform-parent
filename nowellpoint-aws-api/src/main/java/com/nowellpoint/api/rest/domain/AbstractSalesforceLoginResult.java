package com.nowellpoint.api.rest.domain;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.nowellpoint.client.sforce.model.Token;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractSalesforceLoginResult {
	public abstract @Nullable Token getToken();
	
	@Value.Default
	public String getStatus() {
		return Connector.NOT_CONNECTED;
	}
	
	@Value.Default
	public Boolean getIsConnected() {
		return Boolean.FALSE;
	}
}