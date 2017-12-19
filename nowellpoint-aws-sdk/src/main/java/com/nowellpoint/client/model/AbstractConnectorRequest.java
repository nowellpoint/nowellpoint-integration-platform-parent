package com.nowellpoint.client.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractConnectorRequest extends AbstractAuthenticatedRequest {
	public abstract String getName();
	public abstract @Nullable String getType();
	public abstract @Nullable String getClientId();
	public abstract @Nullable String getClientSecret();
	public abstract @Nullable String getUsername();
	public abstract @Nullable String getPassword();
}