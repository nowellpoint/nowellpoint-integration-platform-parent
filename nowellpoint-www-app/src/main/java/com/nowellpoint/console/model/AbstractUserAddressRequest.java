package com.nowellpoint.console.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractUserAddressRequest {
	public abstract String getCity();
	public abstract String getCountryCode();
	public abstract String getPostalCode();
	public abstract String getState();
	public abstract String getStreet();
}