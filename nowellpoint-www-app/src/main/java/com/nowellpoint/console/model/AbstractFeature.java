package com.nowellpoint.console.model;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Feature.class)
@JsonDeserialize(as = Feature.class)
public abstract class AbstractFeature {
	public abstract Integer getSortOrder();
	public abstract String getCode();
	public abstract String getName();
	public abstract @Nullable String getDescription();
	public abstract @Nullable Boolean getEnabled();
	public abstract String getQuantity();
}