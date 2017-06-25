package com.nowellpoint.api.invoice.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", deepImmutablesDetection = true)
public abstract class AbstractService {
	public abstract String getServiceName();
	public abstract Double getUnitPrice();
	public abstract Integer getQuantity();
	public abstract Double getTotalPrice();
}