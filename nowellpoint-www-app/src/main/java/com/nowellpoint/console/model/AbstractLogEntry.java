package com.nowellpoint.console.model;

import java.util.Locale;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = LogEntry.class)
@JsonDeserialize(as = LogEntry.class)
public abstract class AbstractLogEntry {
	public abstract Integer getStatusCode();
	public abstract String getStatusInfo();
	public abstract String getRequestMethod();
	public abstract String getRequestUri();
	public abstract Long getStartTime();
	public abstract Long getDuration();
	
	@Value.Default
	public Locale getLocale() {
		return Locale.getDefault();
	}
}