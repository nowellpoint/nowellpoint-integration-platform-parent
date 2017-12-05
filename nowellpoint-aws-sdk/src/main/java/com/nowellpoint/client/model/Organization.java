package com.nowellpoint.client.model;

import java.util.ArrayList;
import java.util.List;

public class Organization extends AbstractResource {
	
	private String number;
	
	private String name;
	
	private String domain;
	
	private Subscription subscription;
	
	private List<Transaction> transactions;
	
	private List<UserProfile> users;
	
	public Organization() {
		subscription = new Subscription();
		transactions = new ArrayList<Transaction>();
		users = new ArrayList<UserProfile>();
	}

	public String getNumber() {
		return number;
	}

	public String getName() {
		return name;
	}

	public String getDomain() {
		return domain;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}
	
	public List<UserProfile> getUsers() {
		return users;
	}
}