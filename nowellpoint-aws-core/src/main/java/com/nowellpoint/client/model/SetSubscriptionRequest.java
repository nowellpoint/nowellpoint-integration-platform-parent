package com.nowellpoint.client.model;

public class SetSubscriptionRequest {
	
	private String accountProfileId;
	
	private String planId;
	
	public SetSubscriptionRequest() {
		
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
	
	public SetSubscriptionRequest withAccountProfileId(String accountProfileId) {
		setAccountProfileId(accountProfileId);
		return this;
	}
	
	public SetSubscriptionRequest withPlanId(String planId) {
		setPlanId(planId);
		return this;
	}
}