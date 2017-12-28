package com.nowellpoint.client.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractAddressRequest extends AbstractAuthenticatedRequest {
	public abstract @Nullable String getOrganizationId();
	public abstract @Nullable String getUserProfileId();
	public abstract String getStreet();
	public abstract String getCity();
	public abstract @Nullable String getState();
	public abstract String getPostalCode();
	public abstract String getCountryCode();
}