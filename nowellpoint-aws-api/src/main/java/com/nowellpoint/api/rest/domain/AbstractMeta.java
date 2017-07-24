package com.nowellpoint.api.rest.domain;

import javax.annotation.Nullable;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
public abstract class AbstractMeta {
	public abstract String getHref(); 
	public abstract @Nullable String getMethod();
	public abstract @Nullable String getAccepts();
	public abstract @Nullable String getProduces();
	public abstract @Nullable String getRel();
}