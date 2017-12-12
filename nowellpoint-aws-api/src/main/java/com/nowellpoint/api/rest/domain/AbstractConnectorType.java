package com.nowellpoint.api.rest.domain;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = ConnectorType.class)
@JsonDeserialize(as = ConnectorType.class)
public abstract class AbstractConnectorType {
	public abstract String getName();
	public abstract String getGrantType();
	public abstract String getDisplayName();
	public abstract String getAuthEndpoint();
	public abstract String getIconHref();
}