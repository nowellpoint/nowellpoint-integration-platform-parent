package com.nowellpoint.api.rest.domain;

public abstract class AbstractPrice {
	public abstract String getCurrencyIsoCode();
	public abstract String getCurrencySymbol();
	public abstract Double getUnitPrice();
}