package com.nowellpoint.client.model;

public class Plan {
	
	private String localeSidKey;
	
	private String languageLocaleKey;

	private String planName;
	
	private String code;
	
	private String currencyIsoCode;
	
	private String currencySymbol;
	
	private Double unitPrice;
	
	private String billingFrequency;
	
	private String billingFrequencyPer;
	
	private String billingFrequencyUnit;
	
	private String billingFrequencyQuantity;
	
	private Integer transactions;
	
	private String support;
	
	public Plan() {
		
	}

	public String getLocaleSidKey() {
		return localeSidKey;
	}

	public void setLocaleSidKey(String localeSidKey) {
		this.localeSidKey = localeSidKey;
	}

	public String getLanguageLocaleKey() {
		return languageLocaleKey;
	}

	public void setLanguageLocaleKey(String languageLocaleKey) {
		this.languageLocaleKey = languageLocaleKey;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getBillingFrequency() {
		return billingFrequency;
	}

	public void setBillingFrequency(String billingFrequency) {
		this.billingFrequency = billingFrequency;
	}

	public String getBillingFrequencyPer() {
		return billingFrequencyPer;
	}

	public void setBillingFrequencyPer(String billingFrequencyPer) {
		this.billingFrequencyPer = billingFrequencyPer;
	}

	public String getBillingFrequencyUnit() {
		return billingFrequencyUnit;
	}

	public void setBillingFrequencyUnit(String billingFrequencyUnit) {
		this.billingFrequencyUnit = billingFrequencyUnit;
	}

	public String getBillingFrequencyQuantity() {
		return billingFrequencyQuantity;
	}

	public void setBillingFrequencyQuantity(String billingFrequencyQuantity) {
		this.billingFrequencyQuantity = billingFrequencyQuantity;
	}

	public Integer getTransactions() {
		return transactions;
	}

	public void setTransactions(Integer transactions) {
		this.transactions = transactions;
	}

	public String getSupport() {
		return support;
	}

	public void setSupport(String support) {
		this.support = support;
	}	
}