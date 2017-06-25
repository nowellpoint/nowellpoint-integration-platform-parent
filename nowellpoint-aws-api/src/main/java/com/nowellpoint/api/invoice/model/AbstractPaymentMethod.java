package com.nowellpoint.api.invoice.model;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(typeImmutable = "*", deepImmutablesDetection = true)
public abstract class AbstractPaymentMethod {
	public abstract String getCardType();
	public abstract String getLastFour();
}