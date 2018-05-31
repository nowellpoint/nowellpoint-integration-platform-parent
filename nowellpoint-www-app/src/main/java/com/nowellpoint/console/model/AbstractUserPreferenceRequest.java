package com.nowellpoint.console.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractUserPreferenceRequest {
	public abstract String getLocale();
	public abstract String getTimeZone();
}