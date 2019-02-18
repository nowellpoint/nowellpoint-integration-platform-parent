package com.nowellpoint.console.model;

import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractSignUpRequest {
	public abstract @Nullable String getFirstName();
	public abstract String getLastName();
	public abstract String getEmail();
	public abstract String getPlanId();
	
	@Value.Default
	public Locale getLocale() {
		return Locale.getDefault();
	}
	
	@Value.Default
	public String getTimeZone() {
		return TimeZone.getDefault().getID();
	}
}