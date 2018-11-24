package com.nowellpoint.console.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractOrganizationSyncResponse {
	public abstract String getName();
	public abstract String getDomain();
	public abstract Address getAddress();
	public abstract String getOrganizationType();
}