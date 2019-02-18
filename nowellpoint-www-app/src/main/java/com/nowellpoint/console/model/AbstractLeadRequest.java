package com.nowellpoint.console.model;

import java.util.Locale;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractLeadRequest {
	public abstract String getFirstName();
	public abstract String getLastName();
	public abstract String getEmail();
	public abstract String getMessage();
	
	@Value.Default
	public Locale getLocale() {
		return Locale.getDefault();
	}
}