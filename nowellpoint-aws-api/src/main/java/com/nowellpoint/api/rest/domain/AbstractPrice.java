package com.nowellpoint.api.rest.domain;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true)
@JsonSerialize(as = Price.class)
@JsonDeserialize(as = Price.class)
public abstract class AbstractPrice {
	public abstract String getCurrencyIsoCode();
	public abstract String getCurrencySymbol();
	public abstract Double getUnitPrice();
	
	public static Price of(com.nowellpoint.api.model.document.Price source) {
		Price price = ModifiablePrice.create()
				.setCurrencyIsoCode(source.getCurrencyIsoCode())
				.setCurrencySymbol(source.getCurrencySymbol())
				.setUnitPrice(source.getUnitPrice())
				.toImmutable();
		
		return price;
	}
}