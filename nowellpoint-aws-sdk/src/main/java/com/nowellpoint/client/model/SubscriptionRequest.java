package com.nowellpoint.client.model;

public class SubscriptionRequest {
	
	private String userProfileId;
	
	private String planId;
	
	private String paymentMethodToken;
	
	public SubscriptionRequest() {
		
	}

	public String getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(String userProfileId) {
		this.userProfileId = userProfileId;
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

	public SubscriptionRequest withUserProfileId(String userProfileId) {
		setUserProfileId(userProfileId);
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