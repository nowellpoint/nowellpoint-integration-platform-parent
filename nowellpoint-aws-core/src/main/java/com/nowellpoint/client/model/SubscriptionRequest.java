package com.nowellpoint.client.model;

public class SubscriptionRequest {
	
	private String accountProfileId;
	
	private String planId;
	
	private String paymentMethodToken;
	
	public SubscriptionRequest() {
		
	}

	public String getAccountProfileId() {
		return accountProfileId;
	}

	public void setAccountProfileId(String accountProfileId) {
		this.accountProfileId = accountProfileId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getPaymentMethodToken() {
		return paymentMethodToken;
	}

	public void setPaymentMethodToken(String paymentMethodToken) {
		this.paymentMethodToken = paymentMethodToken;
	}

	public SubscriptionRequest withAccountProfileId(String accountProfileId) {
		setAccountProfileId(accountProfileId);
		return this;
	}
	
	public SubscriptionRequest withPlanId(String planId) {
		setPlanId(planId);
		return this;
	}
	
	public SubscriptionRequest withPaymentMethodToken(String paymentMethodToken) {
		setPaymentMethodToken(paymentMethodToken);
		return this;
	}
}