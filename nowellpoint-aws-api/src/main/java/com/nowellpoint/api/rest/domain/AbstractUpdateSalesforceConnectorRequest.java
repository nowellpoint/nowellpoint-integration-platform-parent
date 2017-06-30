package com.nowellpoint.api.rest.domain;

import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*")
public abstract class AbstractUpdateSalesforceConnectorRequest {
	public abstract Optional<String> getName();
	public abstract Optional<String> getTag();
	public abstract Optional<String> getOwnerId();
}