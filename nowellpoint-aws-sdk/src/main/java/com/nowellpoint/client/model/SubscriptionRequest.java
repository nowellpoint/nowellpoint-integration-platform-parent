package com.nowellpoint.client.model;

public class SubscriptionRequest {
	
	private String organizationId;
	
	private String planId;
	
	private String cardholderName;
	
	private String number;
	
	private String expirationMonth;
	
	private String expirationYear;
	
	private String cvv;
	
	public SubscriptionRequest() {
		
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getCardholderName() {
		return cardholderName;
	}

	public void setCardholderName(String cardholderName) {
		this.cardholderName = cardholderName;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getExpirationMonth() {
		return expirationMonth;
	}

	public void setExpirationMonth(String expirationMonth) {
		this.expirationMonth = expirationMonth;
	}

	public String getExpirationYear() {
		return expirationYear;
	}

	public void setExpirationYear(String expirationYear) {
		this.expirationYear = expirationYear;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public SubscriptionRequest withOrganizationId(String organizationId) {
		setOrganizationId(organizationId);
		return this;
	}
	
	public SubscriptionRequest withPlanId(String planId) {
		setPlanId(planId);
		return this;
	}
	
	public SubscriptionRequest withCardholderName(String cardholderName) {
		setCardholderName(cardholderName);
		return this;
	}
	
	public SubscriptionRequest withNumber(String number) {
		setNumber(number);
		return this;
	}
	
	public SubscriptionRequest withExpirationMonth(String expirationMonth) {
		setExpirationMonth(expirationMonth);
		return this;
	}
	
	public SubscriptionRequest withExpirationYear(String expirationYear) {
		setExpirationYear(expirationYear);
		return this;
	}
	
	public SubscriptionRequest withCvv(String cvv) {
		setCvv(cvv);
		return this;
	}
}