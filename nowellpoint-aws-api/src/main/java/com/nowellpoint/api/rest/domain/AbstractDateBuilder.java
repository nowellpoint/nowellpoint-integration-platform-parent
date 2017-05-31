package com.nowellpoint.api.rest.domain;

import java.util.TimeZone;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*")
public abstract class AbstractDateBuilder {
	public abstract Integer getDay();
	public abstract Integer getMinutes();
	public abstract Integer getHours();
	public abstract Integer getSeconds();
	public abstract Integer getMonth();
	public abstract Integer getYear();
	public abstract TimeZone getTimeZone();
}