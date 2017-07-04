package com.nowellpoint.payables.invoice.model;

import java.math.BigDecimal;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", deepImmutablesDetection = true)
public abstract class AbstractService {
	public abstract String getServiceName();
	public abstract BigDecimal getUnitPrice();
	public abstract Integer getQuantity();
	public abstract BigDecimal getTotalPrice();
}