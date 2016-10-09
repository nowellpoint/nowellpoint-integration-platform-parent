package com.nowellpoint.client.model;

public class UpdateSubscriptionRequest {
	
	private String accountProfileId;
	
	private String planCode;
	
	private String currencyIsoCode;
	
	private Double unitPrice;
	
	public UpdateSubscriptionRequest() {
		
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
	
	public UpdateSubscriptionRequest withAccountProfileId(String accountProfileId) {
		setAccountProfileId(accountProfileId);
		return this;
	}
	
	public UpdateSubscriptionRequest withPlanCode(String planCode) {
		setPlanCode(planCode);
		return this;
	}
	
	public UpdateSubscriptionRequest withCurrencyIsoCode(String currencyIsoCode) {
		setCurrencyIsoCode(currencyIsoCode);
		return this;
	}
	
	public UpdateSubscriptionRequest withUnitPrice(Double unitPrice) {
		setUnitPrice(unitPrice);
		return this;
	}
}