package com.nowellpoint.console.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractOrganizationRequest {
	public abstract String getName();
	public abstract String getDomain();
	public abstract String getPlanId();
	public abstract String getEmail();
	public abstract @Nullable String getFirstName();
	public abstract String getLastName();
}