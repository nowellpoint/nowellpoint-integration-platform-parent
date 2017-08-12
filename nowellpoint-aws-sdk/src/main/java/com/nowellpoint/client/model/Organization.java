package com.nowellpoint.client.model;

import java.util.ArrayList;
import java.util.List;

public class Organization extends AbstractResource {
	
	private String number;
	
	private String name;
	
	private String domain;
	
	private Subscription subscription;
	
	private List<Transaction> transactions;
	
	public Organization() {
		subscription = new Subscription();
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

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
}