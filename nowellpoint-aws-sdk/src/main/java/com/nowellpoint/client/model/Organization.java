package com.nowellpoint.client.model;

import java.util.ArrayList;
import java.util.List;

public class Organization extends AbstractResource {
	
	private String number;
	
	private String name;
	
	private String domain;
	
	private Contact billingContact;
	
	private Address billingAddress;
	
	private Subscription subscription;
	
	private CreditCard creditCard;
	
	private List<Transaction> transactions;
	
	public Organization() {
		billingContact = new Contact();
		billingAddress = new Address();
		subscription = new Subscription();
		creditCard = new CreditCard();
		transactions = new ArrayList<Transaction>();
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
}