package com.nowellpoint.api.rest.domain;

public class OrganizationInfo {
	
	private AbstractUserInfo createdBy;
	private AbstractUserInfo lastUpdatedBy;	
	private String number;
	private String domain;
	private Contact billingContact;
	private Address billingAddress;
	private Subscription subscription;
	
	public OrganizationInfo() {
		
	}

	public AbstractUserInfo getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(AbstractUserInfo createdBy) {
		this.createdBy = createdBy;
	}

	public AbstractUserInfo getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(AbstractUserInfo lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Contact getBillingContact() {
		return billingContact;
	}

	public void setBillingContact(Contact billingContact) {
		this.billingContact = billingContact;
	}

	public Address getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}
}