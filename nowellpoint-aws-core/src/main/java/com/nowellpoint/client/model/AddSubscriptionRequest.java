package com.nowellpoint.client.model;

public class AddSubscriptionRequest {
	
	private String accountProfileId;
	
	private String planCode;
	
	private String currencyIsoCode;
	
	private Double unitPrice;
	
	public AddSubscriptionRequest() {
		
	}

	public String getAccountProfileId() {
		return accountProfileId;
	}

	public void setAccountProfileId(String accountProfileId) {
		this.accountProfileId = accountProfileId;
	}

	public String getPlanCode() {
		return planCode;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}

	public String getCurrencyIsoCode() {
		return currencyIsoCode;
	}

	public void setCurrencyIsoCode(String currencyIsoCode) {
		this.currencyIsoCode = currencyIsoCode;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public AddSubscriptionRequest withAccountProfileId(String accountProfileId) {
		setAccountProfileId(accountProfileId);
		return this;
	}
	
	public AddSubscriptionRequest withPlanCode(String planCode) {
		setPlanCode(planCode);
		return this;
	}
	
	public AddSubscriptionRequest withCurrencyIsoCode(String currencyIsoCode) {
		setCurrencyIsoCode(currencyIsoCode);
		return this;
	}
	
	public AddSubscriptionRequest withUnitPrice(Double unitPrice) {
		setUnitPrice(unitPrice);
		return this;
	}
}