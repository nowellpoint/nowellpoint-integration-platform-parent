package com.nowellpoint.console.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractBillingContactRequest {
	public abstract String getFirstName();
	public abstract String getLastName();
	public abstract String getPhone();
	public abstract String getEmail();
}