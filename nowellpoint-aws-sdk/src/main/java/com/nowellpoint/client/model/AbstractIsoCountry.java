package com.nowellpoint.client.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractIsoCountry {
	public abstract String getIso2Code();
	public abstract String getName();
}