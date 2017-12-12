package com.nowellpoint.api.rest.domain;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = ConnectorRequest.class)
@JsonDeserialize(as = ConnectorRequest.class)
public abstract class AbstractConnectorRequest {
	public abstract @Nullable String getName();
	public abstract @Nullable String getType();
	public abstract @Nullable String getClientId();
	public abstract @Nullable String getClientSecret();
	public abstract @Nullable String getUsername();
	public abstract @Nullable String getPassword();
}