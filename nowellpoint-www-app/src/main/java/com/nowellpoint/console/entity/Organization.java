package com.nowellpoint.console.entity;

import org.mongodb.morphia.annotations.Entity;

@Entity(value = "organizations")
public class Organization extends BaseEntity {
	
	private static final long serialVersionUID = -7891914413636330372L;

	private String number;
	
	private String name;
	
	private String domain;

	private String instanceUrl;

	private String encryptedToken;

	private String connectedUser;
	
	private Subscription subscription;
	
	public Organization() {
		
	}

	public Organization(String id) {
		super(id);
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

	public String getInstanceUrl() {
		return instanceUrl;
	}

	public void setInstanceUrl(String instanceUrl) {
		this.instanceUrl = instanceUrl;
	}

	public String getEncryptedToken() {
		return encryptedToken;
	}

	public void setEncryptedToken(String encryptedToken) {
		this.encryptedToken = encryptedToken;
	}

	public String getConnectedUser() {
		return connectedUser;
	}

	public void setConnectedUser(String connectedUser) {
		this.connectedUser = connectedUser;
	}
	
	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}
}