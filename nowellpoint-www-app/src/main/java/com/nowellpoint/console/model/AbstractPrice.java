package com.nowellpoint.console.model;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = Price.class)
@JsonDeserialize(as = Price.class)
public abstract class AbstractPrice {
	public abstract String getCurrencyIsoCode();
	public abstract String getCurrencySymbol();
	public abstract Double getUnitPrice();
	
	public static Price of(com.nowellpoint.console.entity.Price source) {
		return Price.builder()
				.currencyIsoCode(source.getCurrencyIsoCode())
				.currencySymbol(source.getCurrencySymbol())
				.unitPrice(source.getUnitPrice())
				.build();
	}
}