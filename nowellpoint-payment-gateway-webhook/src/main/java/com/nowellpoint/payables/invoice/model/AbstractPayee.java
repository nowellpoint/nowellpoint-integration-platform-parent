package com.nowellpoint.payables.invoice.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", deepImmutablesDetection = true)
public abstract class AbstractPayee {
	public abstract String getCustomerId();
	public abstract String getEmail();
	public abstract String getCompanyName();
	public abstract String getAttentionTo();
	public abstract String getStreet();
	public abstract String getCity();
	public abstract @Nullable String getState();
	public abstract @Nullable String getPostalCode();
	public abstract String getCountry();
}