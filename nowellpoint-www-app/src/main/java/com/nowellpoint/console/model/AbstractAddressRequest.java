package com.nowellpoint.console.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractAddressRequest {
	public abstract String getStreet();
	public abstract String getCity();
	public abstract @Nullable String getState();
	public abstract String getPostalCode();
	public abstract String getCountryCode();
}