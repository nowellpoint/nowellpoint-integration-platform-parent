package com.nowellpoint.api.rest.domain;

public abstract class AbstractFeature {
	public abstract Integer getSortOrder();
	public abstract String getCode();
	public abstract String getName();
	public abstract String getDescription();
	public abstract Boolean getEnabled();
	public abstract String getQuantity();
}