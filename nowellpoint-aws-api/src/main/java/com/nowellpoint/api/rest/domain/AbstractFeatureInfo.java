package com.nowellpoint.api.rest.domain;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = FeatureInfo.class)
@JsonDeserialize(as = FeatureInfo.class)
public abstract class AbstractFeatureInfo {
	public abstract Integer getSortOrder();
	public abstract String getCode();
	public abstract String getName();
	public abstract @Nullable String getDescription();
	public abstract @Nullable Boolean getEnabled();
	public abstract String getQuantity();
}