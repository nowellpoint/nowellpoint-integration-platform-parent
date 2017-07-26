package com.nowellpoint.api.rest.domain;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Meta.class)
@JsonDeserialize(as = Meta.class)
public abstract class AbstractMeta {
	public abstract String getHref(); 
	public abstract @Nullable String getMethod();
	public abstract @Nullable String getAccepts();
	public abstract @Nullable String getProduces();
	public abstract @Nullable String getRel();
}