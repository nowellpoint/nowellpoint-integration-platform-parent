package com.nowellpoint.client.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = ConnectorRequest.class)
public abstract class AbstractConnectorRequest extends AbstractAuthenticatedRequest {
	public abstract @Nullable String getName();
	public abstract @Nullable String getStatus();
	public abstract @Nullable String getType();
	public abstract @Nullable String getClientId();
	public abstract @Nullable String getClientSecret();
	public abstract @Nullable String getUsername();
	public abstract @Nullable String getPassword();
}