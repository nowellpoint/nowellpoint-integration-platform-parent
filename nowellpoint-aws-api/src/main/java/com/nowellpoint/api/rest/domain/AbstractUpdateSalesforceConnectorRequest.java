package com.nowellpoint.api.rest.domain;

import java.util.Optional;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*")
public abstract class AbstractUpdateSalesforceConnectorRequest {
	public abstract @Nullable String getName();
	public abstract Optional<String> getTag();
	public abstract @Nullable UserInfo getOwner();
	public abstract UserInfo getLastUpdatedBy();
}