package com.nowellpoint.api.invoice.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", deepImmutablesDetection = true)
public abstract class AbstractPayee {
	public abstract String getCompanyName();
	public abstract String getAttentionTo();
	public abstract String getStreet();
	public abstract String getCity();
	public abstract String getState();
	public abstract String getPostalCode();
	public abstract String getCountry();
}