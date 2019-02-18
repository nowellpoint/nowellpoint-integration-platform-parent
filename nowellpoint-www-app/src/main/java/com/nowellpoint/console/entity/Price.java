package com.nowellpoint.console.entity;

import java.io.Serializable;

public class Price implements Serializable {

	private static final long serialVersionUID = -5335110075479597841L;
	private String currencyIsoCode;
	private String currencySymbol;
	private Double unitPrice;
	
	public Price() {
		
	}

	public String getCurrencyIsoCode() {
		return currencyIsoCode;
	}

	public void setCurrencyIsoCode(String currencyIsoCode) {
		this.currencyIsoCode = currencyIsoCode;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}
}