package com.nowellpoint.client.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractContactRequest extends AbstractAuthenticatedRequest {
	public abstract String getOrganizationId();
	public abstract String getFirstName();
	public abstract String getLastName();
	public abstract String getEmail();
	public abstract String getPhone();
}