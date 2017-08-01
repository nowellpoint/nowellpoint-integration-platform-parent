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
	private Contact billingContact;
	
	@EmbedOne
	private Address billingAddress;
	
	@EmbedOne
	private Subscription subscription;
	
	@EmbedOne
	private CreditCard creditCard;
	
	@EmbedMany
	private Set<Transaction> transactions;
	
	@EmbedMany
	private Set<ReferenceLink> referenceLinks;
	
	public Organization() {
		billingContact = new Contact();
		billingAddress = new Address();
		subscription = new Subscription();
		creditCard = new CreditCard();
		transactions = new HashSet<>();
		referenceLinks = new HashSet<>();
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

	public Set<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Set<Transaction> transactions) {
		this.transactions = transactions;
	}

	public Set<ReferenceLink> getReferenceLinks() {
		return referenceLinks;
	}

	public void setReferenceLinks(Set<ReferenceLink> referenceLinks) {
		this.referenceLinks = referenceLinks;
	}
}