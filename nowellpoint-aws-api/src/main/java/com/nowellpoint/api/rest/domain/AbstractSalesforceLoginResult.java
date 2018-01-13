package com.nowellpoint.api.rest.domain;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.nowellpoint.client.sforce.model.Token;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractSalesforceLoginResult {
	public abstract @Nullable Token getToken();
	public abstract String getStatus();
	public abstract Boolean getIsConnected();
}