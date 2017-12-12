package com.nowellpoint.client.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractConnectorRequest extends AbstractAuthenticatedRequest {
	public abstract String getName();
	public abstract String getType();
	public abstract String getClientId();
	public abstract String getClientSecret();
	public abstract String getUsername();
	public abstract String getPassword();
}