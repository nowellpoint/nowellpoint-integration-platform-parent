package com.nowellpoint.console.model;

import java.util.Locale;
import java.util.TimeZone;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractIdentityRequest {
	public abstract String getFirstName();
	public abstract String getLastName();
	public abstract String getEmail();
	public abstract String getPassword();
	
	@Value.Default
	public Locale getLocale() {
		return Locale.getDefault();
	}
	
	@Value.Default
	public String getTimeZone() {
		return TimeZone.getDefault().getID();
	}
}