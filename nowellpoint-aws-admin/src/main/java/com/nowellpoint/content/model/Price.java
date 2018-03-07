package com.nowellpoint.content.model;

public class Price {
	
	private String currencyIsoCode;
	private String currencySymbol;
	private Double unitPrice;
	
	public Price() {
		
	}

	public String getCurrencyIsoCode() {
		return currencyIsoCode;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}
}