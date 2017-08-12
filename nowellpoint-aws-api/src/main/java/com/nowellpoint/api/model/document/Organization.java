package com.nowellpoint.api.model.document;

import java.util.HashSet;
import java.util.Set;

import com.nowellpoint.mongodb.annotation.Document;
import com.nowellpoint.mongodb.annotation.EmbedMany;
import com.nowellpoint.mongodb.annotation.EmbedOne;
import com.nowellpoint.mongodb.annotation.Reference;
import com.nowellpoint.mongodb.document.MongoDocument;

@Document(collectionName="organizations")
public class Organization extends MongoDocument {

	private static final long serialVersionUID = 1L;
	
	@Reference(referenceClass = UserProfile.class)
	private UserRef createdBy;
	
	@Reference(referenceClass = UserProfile.class)
	private UserRef lastUpdatedBy;	
	
	private String number;
	
	private String name;
	
	private String domain;
	
	@EmbedOne
	private Subscription subscription;
	
	@EmbedMany
	private Set<Transaction> transactions;
	
	@EmbedOne
	private ReferenceLink referenceLink;
	
	public Organization() {
		subscription = new Subscription();
		transactions = new HashSet<>();
		referenceLink = new ReferenceLink();
	}

	public UserRef getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(UserRef createdBy) {
		this.createdBy = createdBy;
	}

	public UserRef getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(UserRef lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
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

	public Set<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Set<Transaction> transactions) {
		this.transactions = transactions;
	}

	public ReferenceLink getReferenceLink() {
		return referenceLink;
	}

	public void setReferenceLink(ReferenceLink referenceLink) {
		this.referenceLink = referenceLink;
	}
}