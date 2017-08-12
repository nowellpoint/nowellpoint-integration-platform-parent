package com.nowellpoint.client.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractSignUpRequest {
	public abstract String getFirstName();
	public abstract String getLastName();
	public abstract String getEmail();
	public abstract String getCountryCode();
	public abstract @Nullable String getDomain();
	public abstract @Nullable String getPhone();
	public abstract String getPlanId();
}