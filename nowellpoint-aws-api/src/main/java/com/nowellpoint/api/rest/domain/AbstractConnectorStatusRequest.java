package com.nowellpoint.api.rest.domain;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = ConnectorStatusRequest.class)
@JsonDeserialize(as = ConnectorStatusRequest.class)
public abstract class AbstractConnectorStatusRequest {
	public abstract String getStatus();
	public @Nullable abstract String getClientId();
	public @Nullable abstract String getClientSecret();
	public @Nullable abstract String getUsername();
	public @Nullable abstract String getPassword();
}